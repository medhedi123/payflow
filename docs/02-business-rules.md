# PayFlow — Business Rules

## 1. User Roles

PayFlow has three main roles:

- ADMIN
- MERCHANT
- CUSTOMER

## 2. Wallet Rules

- Every CUSTOMER has one wallet.
- Every MERCHANT has one merchant wallet.
- Wallet balance cannot be negative.
- A wallet transaction is immutable after creation.
- Money movement must always create a transaction record.
- Each transaction has a status: PENDING, SUCCESS, FAILED, CANCELLED.
- Supported transaction types:
  - DEPOSIT
  - TRANSFER
  - PAYMENT
  - REFUND
  - WITHDRAWAL
- A wallet can be ACTIVE, BLOCKED, or CLOSED.

## 3. Merchant Rules

- A MERCHANT represents a business using PayFlow.
- A merchant must have a business name.
- A merchant can manage multiple customers.
- A merchant can create invoices.
- A merchant can generate payment links.
- A merchant can generate QR codes for payment.
- A merchant can view payment status.
- A merchant can export reports.
- A merchant can only access their own customers, invoices, and transactions.
- A merchant account can be PENDING, ACTIVE, SUSPENDED, or REJECTED.

## 4. Customer Rules

- A CUSTOMER can have one wallet.
- A customer can pay invoices using wallet balance.
- A customer can send money to another customer.
- A customer can receive money.
- A customer can view their transaction history.
- A customer cannot access merchant dashboard data.

## 5. Invoice Rules

- An invoice belongs to one merchant.
- An invoice belongs to one customer.
- An invoice has a unique invoice number.
- An invoice has an issue date.
- An invoice has a due date.
- An invoice has a total amount.
- An invoice has a currency.
- An invoice can contain one or more invoice items.
- Invoice status can be:
  - DRAFT
  - SENT
  - PARTIALLY_PAID
  - PAID
  - OVERDUE
  - CANCELLED
- A paid invoice cannot be modified.
- Every invoice payment creates a transaction record.

## 6. Payment Link Rules

- A payment link belongs to one merchant.
- A payment link may be linked to an invoice.
- A payment link can have an expiration date.
- A payment link can be ACTIVE, EXPIRED, PAID, or CANCELLED.
- A payment link generates a unique public URL.
- A payment link may be paid only once.

## 7. QR Payment Rules

- A merchant can generate QR codes.
- A QR code can be linked to:
  - an invoice
  - a payment link
  - a fixed amount
- A QR code has a unique identifier.
- A QR code can be ACTIVE, USED, EXPIRED, or DISABLED.

## 8. Notification Rules

Users receive notifications when:

- Invoice is paid
- Payment is received
- Transfer is received
- Wallet balance changes
- Account status changes
- Merchant account is approved or rejected

## 9. Reporting Rules

Merchants can view:

- Daily revenue
- Weekly revenue
- Monthly revenue
- Paid invoices
- Unpaid invoices
- Customer statistics
- Transaction history

Reports can be exported to:

- PDF
- Excel
- CSV

