package com.example.loan_api.service;

import com.example.loan_api.exception.custom.LoanPaymentException;
import com.example.loan_api.model.dto.*;
import com.example.loan_api.model.loan.Loan;
import com.example.loan_api.model.loan.LoanPayment;
import com.example.loan_api.model.loan.LoanPaymentStatus;
import com.example.loan_api.model.loan.LoanType;
import com.example.loan_api.model.user.User;
import com.example.loan_api.repository.LoanPaymentRepository;
import com.example.loan_api.repository.LoanRepository;
import com.example.loan_api.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class LoanService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private final LoanRepository loanRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final UserService userService;

    public List<LoanDTO> getAllByCurrentUser(String email) {
        User user = userService.findByEmail(email);
        return getUserLoanDTOS(user);
    }

    public List<LoanDTO> getAllByUserId(UUID userId) {
        User user = userService.findById(userId);
        return getUserLoanDTOS(user);
    }

    public List<LoanType> getTypes() {
        return loanTypeRepository.findAll();
    }

    public List<ScheduleDTO> getUserSchedule(UUID loanId, UUID userId) {
        User user = userService.findById(userId);
        return createSchedule(loanId, user.getId());
    }

    public List<ScheduleDTO> getSchedule(UUID loanId, String email) {
        User user = userService.findByEmail(email);
        return createSchedule(loanId, user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UUID apply(PostLoanDTO postLoanDTO) {
        User user = userService.findById(postLoanDTO.getUserId());
        LoanType loanType = getLoanType(postLoanDTO.getLoanTypeId());

        if (isUserAppliedForThisLoanType(user, loanType)) {
            throw new IllegalArgumentException("Already applied for this loan!");
        }

        BigDecimal monthlyPaymentAmount = calculateMonthlyPaymentAmount(loanType.getAmount(), loanType.getMonths(), loanType.getInterest());
        Loan loan = Loan.builder()
                .monthlyPaymentAmount(monthlyPaymentAmount)
                .waivedPayment(false)
                .appliedOn(LocalDateTime.now())
                .approvedOn(LocalDateTime.now().plusDays(3))
                .loanType(loanType)
                .user(user)
                .build();
        return loanRepository.save(loan).getId();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void payment(PostPaymentDTO postPaymentDTO, boolean waivePayment) {
        User user = userService.findById(postPaymentDTO.getUserId());
        Loan loan = getLoan(postPaymentDTO.getLoanId());
        LoanPaymentStatus loanPaymentStatus = waivePayment ? LoanPaymentStatus.WAIVED :  LoanPaymentStatus.PAID;
        checkIsLoanPaidOff(loan);

        if (waivePayment) {
            checkIsWaivePaymentApplied(loan);
            loan.setWaivedPayment(true);
        }

        LoanPayment loanPayment = savePayment(user, loan, loanPaymentStatus);
        addPaymentToLoan(loan, loanPayment);
        setLoanPaidOffIfPaymentIsLast(loan);
        loanRepository.save(loan);
    }

    private LoanPayment savePayment(User user, Loan loan, LoanPaymentStatus loanPaymentStatus) {
        BigDecimal interest = calculateMonthlyInterest(loan);
        BigDecimal principal = loan.getMonthlyPaymentAmount().subtract(interest);

        LoanPayment loanPayment = LoanPayment.builder()
                .principal(principal)
                .interest(interest)
                .status(loanPaymentStatus)
                .paidOn(LocalDateTime.now())
                .loan(loan)
                .user(user)
                .build();
       return loanPaymentRepository.save(loanPayment);
    }

    private void addPaymentToLoan(Loan loan, LoanPayment loanPayment) {
        if (loan.getLoanPayments() == null) {
            loan.setLoanPayments(Collections.singletonList(loanPayment));
        }
        loan.getLoanPayments().add(loanPayment);
    }

    private void setLoanPaidOffIfPaymentIsLast(Loan loan) {
        if (loan.getLoanPayments() != null && loan.getLoanType().getMonths() == loan.getLoanPayments().size()) {
            loan.setPaidOffOn(LocalDateTime.now());
        }
    }

    private LoanType getLoanType(UUID loanTypeId) {
        return loanTypeRepository.findById(loanTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Loan type not found with id: " + loanTypeId));
    }

    private Loan getLoan(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with id: " + loanId));
    }

    private Loan getLoan(UUID loanId, UUID userId) {
        return loanRepository.findByIdAndUserId(loanId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not for user with id: " + userId));
    }

    private boolean isUserAppliedForThisLoanType(User user, LoanType loanType) {
        return user.getLoans() != null && user.getLoans()
                .stream()
                .filter(loan -> loan.getPaidOffOn() == null)
                .flatMap(loan -> Stream.of(loan.getLoanType()))
                .anyMatch(loanType1 -> loanType1.getId().equals(loanType.getId()));
    }

    private void checkIsLoanPaidOff(Loan loan) {
        if (loan.getPaidOffOn() != null) {
            throw new LoanPaymentException("Loan is paid off!");
        }
    }

    private void checkIsWaivePaymentApplied(Loan loan) {
        if (loan.getWaivedPayment()) {
            throw new LoanPaymentException("Waived Payment applied to this loan!");
        }
    }

    private BigDecimal calculateMonthlyPaymentAmount(BigDecimal amount, Integer months, BigDecimal interestIn) {
        BigDecimal interest = getBigDecimalInterest(interestIn);
        BigDecimal pow = interest.add(BigDecimal.ONE).pow(months);
        return amount.multiply(interest.multiply(pow))
                .divide(pow.subtract(BigDecimal.ONE), 12, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMonthlyInterest(Loan loan) {
        BigDecimal totalAmount = loan.getLoanType().getAmount();
        BigDecimal principalAmountPaid =  loan.getLoanPayments() == null || loan.getLoanPayments().isEmpty() ? BigDecimal.ZERO :
                loan.getLoanPayments().stream().map(LoanPayment::getPrincipal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return calculateMonthlyInterest(totalAmount, principalAmountPaid, loan.getLoanType().getInterest());
    }

    private BigDecimal calculateMonthlyInterest(BigDecimal totalAmount, BigDecimal principalAmountPaid, BigDecimal interest) {
        return (totalAmount.subtract(principalAmountPaid).multiply(getBigDecimalInterest(interest)));
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBigDecimalInterest(BigDecimal interest) {
        return interest.divide(BigDecimal.valueOf(100), 12, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 12, RoundingMode.HALF_UP);
    }

    private List<LoanDTO> getUserLoanDTOS(User user) {
        return loanRepository.findAllByUserId(user.getId())
                .stream()
                .map(loan -> LoanDTO.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .loanId(loan.getId())
                        .loanType(loan.getLoanType())
                        .paymentStatus(loan.getPaidOffOn() == null ? LoanPaymentStatus.UNPAID.name() : LoanPaymentStatus.PAID.name())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ScheduleDTO> createSchedule(UUID loanId, UUID userId) {
        Loan loan = getLoan(loanId, userId);
        List<LoanPayment> loanPayments = loan.getLoanPayments();
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();

        BigDecimal principalAmountPaid = BigDecimal.ZERO;
        BigDecimal totalInterestPaid = BigDecimal.ZERO;
        int payments = loanPayments == null ? 0 : loanPayments.size();

        for (int i = 0; i < loan.getLoanType().getMonths(); i++) {
            String paymentStatus = i < payments ? loanPayments.get(i).getStatus().name() : LoanPaymentStatus.UNPAID.name();
            BigDecimal interest =  i < payments ? loanPayments.get(i).getInterest() :
                    calculateMonthlyInterest(loan.getLoanType().getAmount(), principalAmountPaid, loan.getLoanType().getInterest());
            BigDecimal principal = loan.getMonthlyPaymentAmount().subtract(interest);

            principalAmountPaid = principalAmountPaid.add(principal);
            totalInterestPaid = totalInterestPaid.add(interest);

            ScheduleDTO scheduleDTO = ScheduleDTO.builder()
                    .paymentDate(loan.getApprovedOn().plusMonths(i+1).format(formatter))
                    .monthlyPayment(round(loan.getMonthlyPaymentAmount()).toString())
                    .principal(round(principal).toString())
                    .interest(round(interest).toString())
                    .totalInterestPaid(round(totalInterestPaid).toString())
                    .remainingBalance(round(loan.getLoanType().getAmount().subtract(principalAmountPaid)).toString())
                    .paymentStatus(paymentStatus)
                    .build();
            scheduleDTOS.add(scheduleDTO);
        }
        return scheduleDTOS;
    }
}
