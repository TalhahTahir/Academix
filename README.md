The vision is changed: teacher will make course (he will set fees, catagory, name, etc)
admin will validate course
teacher will add lectures and documents
push launch button
student enroll

fee paid by student will be stored in LMS’s account
we will have a vault screen for teacher and admin, where they can see their total balance, history, and withdraw it

change admin course declearation
payment logic updation

## course with same name?

## Webhook

create a webhook secret from Srtipe Website -> developer screen
paste it to application properties -> stripe.webhook-secret = ....
install Stripe CLI

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

1. _Phase 1_: accept only card payments | no wallets | use stripe in backend | only Async

2. _Phase 2_: accept cards, bank Transfers, Mobile Wallets | have wallets

---

## TO-DO:

1. PaymentServiceImpl is Handling work of other fields, split the work

---

#### Use pre-made dedicated frameworks / libraries for Auditing.

---

## payment Flow:

1. ek controller bnao processes initiatation k liye, jo data collect kry or payment methods ko call kry (payment controller)
2. payment method me:

   1. input ki authenticity validate kro
   2. intent create kro
   3. payment intent bnao
   4. payment record save kro (jo bhi intent status aye)
   5. payment intent se id or client secret le kr return kro

3. frontend se Stripe ka confirmCardPayment hit kro
4. method result se UI update kro (payment Succeed / failed)
5. Webhooks receive krne k liye Webhook Controller bnao jisme:
   1. Webhook client secret ho
   2. Stripe ke incomming se seccess / fail extract kro
   3. result k according Business Logic apply kro

---

#### Handle real storing for lectures and documents

---

frontend = React.js

---

### Task ahead:

    integrate:
        Vaulting
        Payment Detailing
        Stripe Eventing
        Other Business Logic
    with payment flow
    test and debug it

    Do Vaulting:
        Distribute the shares (Admin & Teacher)
        make correct & complete flow

### Code with Flow

1. Teacher Register ✅
2. Automatic Vault Creation ✅
3. Teacher created course (course = DRAFT) ✅
4. Admin views courses in different states and takes action (rejected) ✅
5. teacher modified course ✅
6. admin accepted ✅
7. teacher started developing it, then launched ✅
8. student Registered ✅
9. student see course ✅
10. enrolls ✅ (code clean and meaningfull)
11. share Distributation(Vaulting) ✅
12. course progress, stats
13. Certification ✅(marking remaining)

---

## Copilot Suggestions:

Top missing pieces to complete step 12 (course progress, stats)

- Expose progress endpoints
  - Student actions:
    - POST to mark a lecture completed (calls ProgressService.markLectureCompleted)
    - POST to mark a document completed (calls ProgressService.markDocumentCompleted)
  - Queries:
    - GET enrollment progress summary (percentage, completed/total lectures & documents, last activity)
    - Optional: GET list of completed lecture/document IDs per enrollment
- Auto-complete enrollment and award certificate
  - When progress reaches 100% (and any exam requirement is satisfied if you want it), update Enrollment.status to COMPLETED and trigger CertificateService.awardCertificate(enrollmentId)
  - Right now ProgressService updates only completionPercentage; it never sets COMPLETED or awards a certificate
- Course/teacher stats endpoints
  - Per-course stats for a teacher: enrollments, active students, avg completion %, completions count, revenue (if available), last 30 days activity
  - Optional: student-level breakdown (to spot who is stuck)
- Admin stats extension
  - AdminDashboardService exists. Consider adding time-series metrics (weekly enrollments, completions), revenue totals, and filtering by date ranges

Important correctness and robustness fixes

- ContentServiceImpl ownership checks use the wrong ID
  - updateContent/deleteContent call teacherOwnership(userid, contentId), but teacherOwnership expects a courseId. Fetch content, then pass its courseId
- OptionServiceImpl single-correct-option rule
  - Currently throws only if count > 1. It should reject when count >= 1 before adding another correct option
- AttemptAnswerServiceImpl
  - Prevent duplicate answers for the same attempt+question (either update existing answer or enforce a unique constraint on (attempt_id, question_id))
  - Disallow submissions after Attempt.completedAt is set
- AttemptServiceImpl.submitAttempt scoring
  - Denominator should be total questions in the exam, not just number of answers provided; otherwise partial answers inflate scores
  - Consider persisting the score on Attempt and/or Enrollment; and set Attempt.completedAt once, locking further answers
- ExamServiceImpl.checkExam update path
  - It calls enrollmentService.updateEnrollment with a DTO that likely has null fields; updateEnrollment calls enrollmentDTO.getEnrollmentDate().toInstant() which can NPE
  - Prefer a focused method to update only marks (or use repo directly) to avoid partial DTO issues
  - Also, this duplicates scoring logic in AttemptServiceImpl — consolidate scoring into a single path
- AttemptServiceImpl.startAttempt enrollment null safety
  - enrollmentService.enrollmentValidation can return null, causing mapper/NPEs; explicitly check enrollment exists and handle “not enrolled”
- ProgressServiceImpl completion rule
  - Add: if new percentage >= 100, set Enrollment.status = COMPLETED and (optionally) award certificate automatically

Payment and distribution integration points

- EnrollmentServiceImpl.enrollStudent currently bypasses PaymentService (commented out). For production:
  - Tie initiatePayment -> requires_action or success -> finalizeEnrollment via webhook/callback
  - On successful payment, credit teacher vault and platform share if your “share distribution” is already implemented elsewhere
- Webhook handling
  - Implement Stripe webhook to mark Payment status and then finalize enrollment + distribution (this makes the flow resilient to client drop-offs)

Quality-of-life improvements

- Controllers for the new endpoints (progress, stats) and basic security
- Thresholds and policies
  - If you require exam completion to complete a course, encode that rule when marking enrollment completed (e.g., progress 100% AND passed required exam)
- Student/teacher dashboards
  - Student: list of enrolled courses with progress, next recommended lecture/document
  - Teacher: course performance over time, stuck students, content engagement

Quick code-change checklist

- Fix teacherOwnership misuse:
  - In ContentServiceImpl.updateContent/deleteContent, resolve content -> courseId, then call teacherOwnership(userid, courseId)
- Enforce single correct option:
  - In OptionServiceImpl.addOption, change the check to “if dto.isCorrect() and count >= 1 then throw”
- Prevent duplicate attempt answers:
  - Before saving a new AttemptAnswer, check repo for existing by (attemptId, questionId). Update instead of insert, and block if attempt.completedAt != null
- Use exam question count for scoring:
  - In AttemptServiceImpl.submitAttempt, load total questions for attempt.getExam(), compute percentage against that count
- Harden ExamServiceImpl.checkExam:
  - Replace updateEnrollment call with a method that updates only marks; avoid using DTOs with nulls

If you’d like, I can:

- Propose specific controller endpoints for progress and stats (with request/response models)
- Draft patches for the bugs above
- Wire auto-completion + certificate awarding
- Sketch the webhook flow for Stripe to connect payments -> enrollment -> vault distribution

### Final Touches

    refactor the code according to professional Standards

## Supabase Upload/Get

#### Step1:
  POST: http://localhost:8081/api/files/initiate-signed-upload

  BODY (JSON):
  {
  "teacherId": 1,
  "courseId": 10,
  "fileName": "intro.mp4",
  "mimeType": "video/mp4",
  "sizeBytes": 123456,
  "type": "LECTURE"
  }

  HEADER: Content-type: application/json
---
#### Step2:
  Use the returned signedUploadUrl: find it in result of first step

  PUT {signedUploadUrl}

  Body → binary (choose your PNG file)
  Header: Content-Type: image/png

  OUTPUT: KEY
---
#### Step3:
  POST http://localhost:8081/api/files/{storedFileId}/mark-ready
---
#### Step4:
  GET http://localhost:8081/api/files/{storedFileId}/signed-download?expiresIn=600
  
