The vision is changed: teacher will make course (he will set fees, catagory, name, etc)
admin will validate course 
teacher will add lectures and documents
push launch button
student enroll

fee paid by student will be stored in LMS’s account
we will have a vault screen for teacher and admin, where they can see their total balance, history, and withdraw it

change admin course declearation
payment logic updation

course with same name?
---
### Payment:
keep async flow:
1. payment initiate
2. gateway give response about money deduction or process success
3. user get notification & access to course
4. other async chores of payments are handled in backend

by this i dont think we need sync flow because using above strategy:
user gets fast service(sync feature)
Platform gets security, scalibility, cost-friendly service(async feature)

---

### Payment Service has 2 phases:

1. *Phase 1*: accept only card payments | no wallets | use stripe in backend | only Async

2. *Phase 2*: accept cards, bank Transfers, Mobile Wallets | have wallets  

---
## TO-DO:
1. PaymentServiceImpl is Handling work of other fields, split the work
---
#### Use pre-made dedicated frameworks / libraries for Auditing. 
---
## payment Flow:
1. ek controller bnao processes initiatation k liye, jo data collect kry or payment methods ko call kry (payment controller)
2. payment method me:
    1.  input ki authenticity validate kro
    2.  intent create kro
    3.  payment intent bnao
    4.  payment record save kro (jo bhi intent status aye)
    5.  payment intent se id or client secret le kr return kro

3. frontend se Stripe ka confirmCardPayment hit kro
4. method result se UI update kro (payment Succeed / failed)
5. Webhooks receive krne k liye Webhook Controller bnao jisme:
    1.  Webhook client secret ho
    2.  Stripe ke incomming se seccess / fail extract kro
    3.  result k according Business Logic apply kro

---
#### Handle real storing for lectures and documents
---
frontend = React.js