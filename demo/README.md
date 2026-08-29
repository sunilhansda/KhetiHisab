KhetiHisab 🚜

KhetiHisab is a small tractor-business management application designed to track customers, cultivation work, drivers, payments, and outstanding dues.

The application is designed around a simple principle:

Record the cultivation work that was actually performed and record every payment that was actually received. Calculate outstanding dues from those records instead of maintaining installment records.

1. Business Scenario

Suppose we provide cultivation services to a customer named Ramesh.

Ramesh has three separate cultivation jobs:

Job

Work

Amount

Job 101

Farm cultivation

₹2,000

Job 102

Farm cultivation

₹4,000

Job 103

Farm cultivation

₹1,000

Total



₹7,000

Ramesh does not necessarily pay the entire ₹7,000 at once.

He pays:

First payment: ₹3,000

Later payment: ₹2,500

Later payment: ₹1,500

The application records each payment separately and allocates the payment amount to one or more jobs.

This allows us to determine:

Total amount billed

Total amount paid

Amount remaining

Job-wise outstanding amount

Customer-wise outstanding amount

Complete payment history

2. Database Structure

The application currently has five main tables:

customer
|
| 1 : N
|
v
cultivation_job
|
| N : 1
|
v
driver


payment
|
| 1 : N
|
v
payment_allocation
^
|
| N : 1
|
cultivation_job

Another way to visualize the complete flow:

                         +----------------+
                         |    CUSTOMER    |
                         +----------------+
                           |            |
                         1:N          1:N
                           |            |
                           v            v
                  +----------------+  +----------------+
                  | CULTIVATION_JOB|  |    PAYMENT     |
                  +----------------+  +----------------+
                    |            |          |
                  N:1           1:N        1:N
                    |            |          |
                    v            v          v
              +---------+  +----------------------+
              | DRIVER  |  | PAYMENT_ALLOCATION   |
              +---------+  +----------------------+
                               |
                               | N:1
                               |
                               v
                        CULTIVATION_JOB

3. Why There Is No Farm Table

The business does not maintain permanent farm records with farm names, IDs, or other stable information.

The "farm" is simply the piece of land where a particular cultivation job was performed.

Therefore, we don't create a separate farm table.

Instead, information relevant to the work can be stored directly on cultivation_job, such as:

Area

Area unit

Location

Description

Work type

Work date

For example:

Job 101
--------------------------
Customer: Ramesh
Work: Ploughing
Area: 2 acres
Location: Near river
Amount: ₹2,000

If the same customer uses a different piece of land later, we simply create another cultivation job.

This keeps the data model simple and avoids creating a permanent entity for something that is dynamic.

4. Customer Table

Table:

customer

Purpose:

Stores the customers for whom cultivation work is performed.

Important fields

Column

Purpose

customer_id

Unique customer ID

name

Customer name

phone

Contact number

address

Customer address

created_at

Record creation timestamp

Example

customer_id: 1
name: Ramesh
phone: 9876543210

A customer can have multiple cultivation jobs and multiple payments.

Relationship:

Customer 1 ---- N Cultivation Jobs

Customer 1 ---- N Payments

5. Driver Table

Table:

driver

Purpose:

Stores the drivers who operate the tractor.

Important fields

Column

Purpose

driver_id

Unique driver ID

name

Driver name

phone

Driver contact

address

Driver address

created_at

Record creation timestamp

Example

driver_id: 1
name: Raju
phone: 9876543211

A driver can work on many cultivation jobs.

Because one job is normally handled by one driver, driver_id is directly stored in cultivation_job.

Relationship:

Driver 1 ---- N Cultivation Jobs

6. Cultivation Job Table

Table:

cultivation_job

This is the central business table.

Every time cultivation work is performed, one job record is created.

Important fields

Column

Purpose

job_id

Unique job ID

customer_id

Customer for whom work was performed

driver_id

Driver who performed the work

work_type

Type of work

work_date

Date of work

area

Area cultivated

area_unit

Acre, hectare, etc.

rate

Rate, if applicable

total_amount

Total amount charged for the job

location

Optional location

description

Additional job information

notes

Additional notes

created_at

Record creation timestamp

Example

Job 101
----------------------------
Customer     : Ramesh
Driver       : Raju
Work Type    : PLOUGHING
Work Date    : 2026-08-20
Area         : 2 acres
Rate         : ₹1,000/acre
Total Amount : ₹2,000

Another job:

Job 102
----------------------------
Customer     : Ramesh
Driver       : Raju
Work Type    : PLOUGHING
Work Date    : 2026-08-21
Area         : 4 acres
Rate         : ₹1,000/acre
Total Amount : ₹4,000

And:

Job 103
----------------------------
Customer     : Ramesh
Driver       : Mahesh
Work Type    : THRESHING
Work Date    : 2026-08-22
Area         : 1 acre
Total Amount : ₹1,000

Therefore:

Ramesh
|
+-- Job 101 -> ₹2,000
|
+-- Job 102 -> ₹4,000
|
+-- Job 103 -> ₹1,000
-------
₹7,000

7. Payment Table

Table:

payment

The payment table represents actual money received from a customer.

It does NOT represent an installment.

If a customer gives ₹3,000 today, we create one payment record for ₹3,000.

If the customer gives another ₹2,000 next week, we create another payment record.

Important fields

Column

Purpose

payment_id

Unique payment ID

customer_id

Customer who made the payment

payment_date

Date money was received

amount

Actual amount received

payment_method

CASH, UPI, BANK_TRANSFER, etc.

transaction_reference

UPI/bank transaction reference

notes

Additional information

created_at

Record creation timestamp

Example

Ramesh pays ₹3,000:

Payment #1
----------------------------
Customer       : Ramesh
Payment Date   : 2026-08-26
Amount         : ₹3,000
Method         : CASH

Later he pays ₹2,500:

Payment #2
----------------------------
Customer       : Ramesh
Payment Date   : 2026-09-05
Amount         : ₹2,500
Method         : UPI
Reference      : UPI123456

8. Why Payment Does Not Have job_id

A payment does not necessarily belong to one job.

Example:

Job 101 = ₹2,000
Job 102 = ₹4,000
Job 103 = ₹1,000

Total Due = ₹7,000

Ramesh gives us:

₹3,000

He may say:

"Adjust this against my pending amount."

That ₹3,000 can be applied to multiple jobs:

₹2,000 -> Job 101
₹1,000 -> Job 102

Therefore:

One Payment ---> Multiple Jobs
One Job     ---> Multiple Payments

This is why we need payment_allocation.

9. Payment Allocation Table

Table:

payment_allocation

This table tells us:

"How much of a particular payment was applied to a particular job?"

Important fields

Column

Purpose

allocation_id

Unique allocation ID

payment_id

Payment being allocated

job_id

Job receiving the allocation

amount

Amount allocated to that job

created_at

Creation timestamp

10. Payment Allocation Example

Ramesh owes:

Job 101 -> ₹2,000
Job 102 -> ₹4,000
Job 103 -> ₹1,000

Total -> ₹7,000

Ramesh pays ₹3,000.

Payment table:

payment_id = 1
customer_id = 1
amount = ₹3,000

Allocation:

payment_id | job_id | amount
-----------+--------+-------
1          | 101    | 2000
1          | 102    | 1000

Meaning:

Payment #1 = ₹3,000

              +-- ₹2,000 --> Job 101
              |
Payment #1 ---+
|
+-- ₹1,000 --> Job 102

Now the job balances are:

Job 101
Total = ₹2,000
Paid  = ₹2,000
Due   = ₹0

Job 102
Total = ₹4,000
Paid  = ₹1,000
Due   = ₹3,000

Job 103
Total = ₹1,000
Paid  = ₹0
Due   = ₹1,000

Total:

Total Work = ₹7,000
Total Paid = ₹3,000
Total Due  = ₹4,000

11. Multiple Partial Payments

Suppose Ramesh later pays ₹2,500.

We create a new payment:

Payment #2 = ₹2,500

Allocate it:

Job 102 -> ₹2,500

Now:

Job 101
Total = ₹2,000
Paid  = ₹2,000
Due   = ₹0

Job 102
Total = ₹4,000
Paid  = ₹3,500
Due   = ₹500

Job 103
Total = ₹1,000
Paid  = ₹0
Due   = ₹1,000

Overall:

Total Work = ₹7,000
Total Paid = ₹5,500
Total Due  = ₹1,500

Later Ramesh pays ₹1,500:

Payment #3 = ₹1,500

Job 102 -> ₹500
Job 103 -> ₹1,000

Everything is now settled:

Total Work = ₹7,000
Total Paid = ₹7,000
Total Due  = ₹0

12. No Installment Table

We intentionally do NOT have:

installment
installment_plan

The application doesn't need to know:

1st installment
2nd installment
3rd installment

Instead, it records actual payments:

Payment #1 -> ₹3,000
Payment #2 -> ₹2,500
Payment #3 -> ₹1,500

This is more flexible because customers can pay:

Full amount

Partial amount

Multiple times

Irregular amounts

Different amounts on different dates

The system simply calculates the remaining balance.

13. How Outstanding Amount Is Calculated

For a job:

Outstanding Amount =
Job Total Amount
-
Sum of Allocated Payments

Example:

Job Total = ₹4,000

Payment allocations:
₹1,000
₹2,500

Total Paid = ₹3,500

Outstanding = ₹4,000 - ₹3,500
= ₹500

We do not need to store:

amount_paid
amount_due
status

on the job because those values can be derived from the actual payment records.

14. Customer-Level Outstanding

To calculate how much a customer owes overall:

Customer Total Due =
Sum of all job amounts
-
Sum of all payment allocations

Example:

Ramesh's Jobs

Job 101 -> ₹2,000
Job 102 -> ₹4,000
Job 103 -> ₹1,000

Total = ₹7,000

Payments:

Payment #1 -> ₹3,000
Payment #2 -> ₹2,500

Total paid:

₹5,500

Outstanding:

₹7,000 - ₹5,500 = ₹1,500

15. Payment Flow

When a customer makes a payment, the application should follow this flow:

Customer makes payment
|
v
Create Payment
|
v
Validate payment amount
|
v
Find customer's outstanding jobs
|
v
Allocate payment to jobs
|
v
Calculate remaining balances

Example:

Customer: Ramesh
Payment: ₹3,000

Outstanding:

Job 101 -> ₹2,000
Job 102 -> ₹4,000
Job 103 -> ₹1,000

Allocate:

Job 101 -> ₹2,000
Remaining payment -> ₹1,000

Job 102 -> ₹1,000
Remaining payment -> ₹0

16. Automatic Payment Allocation

The application can optionally allocate payments automatically.

For example, use the oldest outstanding jobs first:

Outstanding jobs:

Job 101 -> ₹2,000
Job 102 -> ₹4,000
Job 103 -> ₹1,000

Customer pays ₹3,000

Algorithm:

remainingPayment = ₹3,000

Job 101:
due = ₹2,000
allocate ₹2,000
remaining = ₹1,000

Job 102:
due = ₹4,000
allocate ₹1,000
remaining = ₹0

Stop

This is a good candidate for the Spring Boot service layer.

17. Important Business Validations

The application should enforce the following rules.

Payment must be positive

Payment amount > 0

Allocation must be positive

Allocation amount > 0

Allocation cannot exceed payment

Example:

Payment = ₹3,000

Invalid:
Job 101 allocation = ₹3,500

Job cannot receive more than its outstanding amount

Example:

Job Total = ₹2,000
Already Paid = ₹1,500
Outstanding = ₹500

Invalid allocation = ₹1,000

Total allocations cannot exceed the payment

Example:

Payment = ₹3,000

Job 101 -> ₹2,000
Job 102 -> ₹1,500

Total allocation = ₹3,500

Invalid

These validations should primarily be handled in the service layer using a transaction.

18. JPA Entity Relationships

The Java/Spring Boot entities follow these relationships:

Customer
|
| @OneToMany
v
CultivationJob
|
| @ManyToOne
v
Driver

And:

Customer
|
| @OneToMany
v
Payment
|
| @OneToMany
v
PaymentAllocation
|
| @ManyToOne
v
CultivationJob

So:

Customer 1 ---- N CultivationJob
Customer 1 ---- N Payment
Driver   1 ---- N CultivationJob

Payment  1 ---- N PaymentAllocation
Job      1 ---- N PaymentAllocation

19. Entity-Level Structure

Customer

Customer
customerId
name
phone
address
createdAt

Relationships:

List<CultivationJob> cultivationJobs;
List<Payment> payments;

Driver

Driver
driverId
name
phone
address
createdAt

Relationship:

List<CultivationJob> cultivationJobs;

CultivationJob

CultivationJob
jobId
customer
driver
workType
workDate
area
areaUnit
rate
totalAmount
location
description
notes
createdAt

Relationship:

List<PaymentAllocation> paymentAllocations;

Payment

Payment
paymentId
customer
paymentDate
amount
paymentMethod
transactionReference
notes
createdAt

Relationship:

List<PaymentAllocation> allocations;

PaymentAllocation

PaymentAllocation
allocationId
payment
job
amount
createdAt

20. Example Complete Data

Customer

ID: 1
Name: Ramesh
Phone: 9876543210

Drivers

ID: 1
Name: Raju

ID: 2
Name: Mahesh

Jobs

Job 101
Customer: Ramesh
Driver: Raju
Work: PLOUGHING
Amount: ₹2,000

Job 102
Customer: Ramesh
Driver: Raju
Work: PLOUGHING
Amount: ₹4,000

Job 103
Customer: Ramesh
Driver: Mahesh
Work: THRESHING
Amount: ₹1,000

Payments

Payment 1
Customer: Ramesh
Amount: ₹3,000
Date: 2026-08-26
Method: CASH

Payment 2
Customer: Ramesh
Amount: ₹2,500
Date: 2026-09-05
Method: UPI

Payment 3
Customer: Ramesh
Amount: ₹1,500
Date: 2026-09-15
Method: CASH

Allocations

Payment 1
Job 101 -> ₹2,000
Job 102 -> ₹1,000

Payment 2
Job 102 -> ₹2,500

Payment 3
Job 102 -> ₹500
Job 103 -> ₹1,000

Final state:

Job 101
₹2,000 / ₹2,000 -> PAID

Job 102
₹4,000 / ₹4,000 -> PAID

Job 103
₹1,000 / ₹1,000 -> PAID

Customer balance:

Total billed : ₹7,000
Total paid   : ₹7,000
Outstanding  : ₹0

21. Recommended Application Layers

For Spring Boot:

Controller
|
v
Service
|
v
Repository
|
v
PostgreSQL

A more complete structure:

com.example.KhetiHisab
│
├── controller
│   ├── CustomerController
│   ├── DriverController
│   ├── CultivationJobController
│   └── PaymentController
│
├── service
│   ├── CustomerService
│   ├── DriverService
│   ├── CultivationJobService
│   └── PaymentService
│
├── repository
│   ├── CustomerRepository
│   ├── DriverRepository
│   ├── CultivationJobRepository
│   ├── PaymentRepository
│   └── PaymentAllocationRepository
│
├── entity
│   ├── Customer
│   ├── Driver
│   ├── CultivationJob
│   ├── Payment
│   └── PaymentAllocation
│
└── dto
├── CustomerDto
├── DriverDto
├── CultivationJobDto
├── PaymentDto
└── PaymentAllocationDto

22. Money Handling

All monetary values should use:

BigDecimal

Do NOT use:

double
float

Example:

private BigDecimal totalAmount;
private BigDecimal amount;

PostgreSQL:

NUMERIC(12,2)

This avoids floating-point precision problems when handling money.

23. Transaction Handling

Creating a payment and its allocations should happen in one database transaction.

Conceptually:

@Transactional
public void recordPayment(...) {

    // 1. Create payment

    // 2. Find outstanding jobs

    // 3. Allocate payment

    // 4. Validate allocation

    // 5. Save allocations
}

If something fails during allocation, the entire operation should roll back.

This prevents situations such as:

Payment saved = ₹3,000
Allocation partially saved
Application fails

The database should either contain the complete payment operation or none of it.

24. Summary

The final design is intentionally simple:

+-------------+
|  CUSTOMER   |
+-------------+
|
| 1:N
v
+-------------------+
| CULTIVATION_JOB   |
+-------------------+
^
|
| N:1
|
+-------------+
|   DRIVER    |
+-------------+


+-------------+
|   PAYMENT   |
+-------------+
|
| 1:N
v
+----------------------+
| PAYMENT_ALLOCATION   |
+----------------------+
|
| N:1
v
+-------------------+
| CULTIVATION_JOB   |
+-------------------+

Core principle

Cultivation Job
= What work was performed and what amount is due

Payment
= What money was actually received

Payment Allocation
= Which job that received money was applied to

Outstanding
= Job Amount - Allocated Payments

This structure avoids unnecessary installment and farm tables while still supporting partial payments, multiple payments, payments covering multiple jobs, complete payment history, and customer/job-level outstanding balances.