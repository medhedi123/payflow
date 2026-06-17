# PayFlow — Database Design

## Core Entities

1. User
2. Merchant
3. Customer
4. Wallet
5. Transaction
6. Invoice
7. InvoiceItem
8. PaymentLink
9. QRCode
10. Notification
11. AuditLog

---

## Entity Relationships

User
 ├── Merchant (optional)
 ├── Customer (optional)
 └── Wallet (1:1)

Merchant
 ├── Customers (1:N)
 ├── Invoices (1:N)
 ├── PaymentLinks (1:N)
 └── Merchant Wallet (1:1)

Customer
 ├── Wallet (1:1)
 └── Invoices (1:N)

Invoice
 ├── InvoiceItems (1:N)
 ├── PaymentLink (1:1)
 └── Customer (N:1)

Wallet
 └── Transactions (1:N)

 ## User

Fields:

- id
- firstName
- lastName
- email
- password
- role
- status
- createdAt
- updatedAt

Role:

- ADMIN
- MERCHANT
- CUSTOMER

Status:

- ACTIVE
- INACTIVE
- SUSPENDED

## Merchant

Fields:

- id
- businessName
- businessEmail
- phone
- address
- registrationNumber
- status
- createdAt

Status:

- PENDING
- ACTIVE
- REJECTED
- SUSPENDED

## Merchant

Fields:

- id
- businessName
- businessEmail
- phone
- address
- registrationNumber
- status
- createdAt

Status:

- PENDING
- ACTIVE
- REJECTED
- SUSPENDED

## Customer

Fields:

- id
- firstName
- lastName
- email
- phone
- address
- createdAt

## Wallet

Fields:

- id
- balance
- currency
- status
- createdAt

Status:

- ACTIVE
- BLOCKED
- CLOSED

## Transaction

Fields:

- id
- reference
- amount
- currency
- type
- status
- description
- senderWalletId
- receiverWalletId
- createdAt

Type:

- DEPOSIT
- TRANSFER
- PAYMENT
- REFUND
- WITHDRAWAL

Status:

- PENDING
- SUCCESS
- FAILED
- CANCELLED

## Invoice

Fields:

- id
- invoiceNumber
- merchantId
- customerId
- issueDate
- dueDate
- subtotal
- taxAmount
- totalAmount
- currency
- status
- createdAt
- updatedAt

Status:

- DRAFT
- SENT
- PARTIALLY_PAID
- PAID
- OVERDUE
- CANCELLED

## InvoiceItem

Fields:

- id
- invoiceId
- description
- quantity
- unitPrice
- totalPrice

## PaymentLink

Fields:

- id
- reference
- merchantId
- invoiceId
- amount
- currency
- publicUrl
- expiresAt
- status
- createdAt

Status:

- ACTIVE
- EXPIRED
- PAID
- CANCELLED

## QRCode

Fields:

- id
- reference
- merchantId
- invoiceId
- paymentLinkId
- amount
- currency
- status
- createdAt

Status:

- ACTIVE
- USED
- EXPIRED
- DISABLED

## Notification

Fields:

- id
- userId
- title
- message
- type
- isRead
- createdAt

## AuditLog

Fields:

- id
- userId
- action
- entityType
- entityId
- ipAddress
- createdAt

