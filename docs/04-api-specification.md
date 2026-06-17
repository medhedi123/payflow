# PayFlow — API Specification

Base URL:

`/api/v1`

## Authentication

### Register Customer

`POST /auth/register/customer`

### Register Merchant

`POST /auth/register/merchant`

### Login

`POST /auth/login`

---

## Users

### Get Current User

`GET /users/me`

---

## Wallets

### Get My Wallet

`GET /wallets/me`

### Get Wallet Transactions

`GET /wallets/me/transactions`

### Transfer Money

`POST /wallets/transfer`

---

## Merchants

### Get My Merchant Profile

`GET /merchants/me`

### Update Merchant Profile

`PUT /merchants/me`

---

## Customers

### Create Customer

`POST /customers`

### Get My Customers

`GET /customers`

### Get Customer By ID

`GET /customers/{id}`

### Update Customer

`PUT /customers/{id}`

### Delete Customer

`DELETE /customers/{id}`

---

## Invoices

### Create Invoice

`POST /invoices`

### Get My Invoices

`GET /invoices`

### Get Invoice By ID

`GET /invoices/{id}`

### Update Invoice

`PUT /invoices/{id}`

### Cancel Invoice

`PATCH /invoices/{id}/cancel`

---

## Payment Links

### Create Payment Link

`POST /payment-links`

### Get Payment Link Public Page

`GET /public/pay/{reference}`

### Pay Payment Link With Wallet

`POST /public/pay/{reference}/wallet`

---

## QR Codes

### Generate QR Code

`POST /qr-codes`

### Get QR Code Details

`GET /qr-codes/{reference}`

---

## Dashboard

### Merchant Dashboard Statistics

`GET /dashboard/merchant`

---

## Admin

### Get All Users

`GET /admin/users`

### Get All Merchants

`GET /admin/merchants`

### Approve Merchant

`PATCH /admin/merchants/{id}/approve`

### Suspend Merchant

`PATCH /admin/merchants/{id}/suspend`

### Get All Transactions

`GET /admin/transactions`