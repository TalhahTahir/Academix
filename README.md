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
###Payment:
keep async flow:
1. payment initiate
2. gateway give response about money deduction or process success
3. user get notification & access to course
4. other async chores of payments are handled in backend

by this i dont think we need sync flow because using above strategy:
user gets fast service(sync feature)
Platform gets security, scalibility, cost-friendly service(async feature)

---

Payment Service has 2 phases:

1. Phase 1: accept only card payments | no wallets | use stripe in backend | only Async

2. Phase 2: accept cards, bank Transfers, Mobile Wallets | have wallets  

---
TO-DO:
1. PaymentServiceImpl is Handling work of other fields, split the work