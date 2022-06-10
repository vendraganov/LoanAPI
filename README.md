# LoanAPI

API for requesting a loan and ability for making a schedule payments.

Have 2 roles with a different set of APIs.

User

Rest API for the role User should allow:

API function Description
Get a loan ask the application for a loan. It is auto
approved and it is returned to the user
Get the loan schedule get the schedule for a loan this user owns
Make a repayment make a payment for a loan that this user owns

Admin

Rest API for the role Admin should allow:

API function Description
Get the loan schedule get the schedule for a loan this user owns
Make a repayment make a payment for any loan
Forgive(cancel) a repayment Forgive a payment for any loan



Admin login credentials: username: admin@bg password: admin
User login credentials: username: user@bg password: password

API Documentation http://localhost:8080/swagger-ui.html

DB credentials: http://localhost:8080/h2-console username: admin password: admin
