# Pregaview: A Multi-Parametric Decision Support System for Maternal Health Monitoring

Pregaview is an integrated maternal healthcare system designed to support early detection of high-risk pregnancy conditions through continuous monitoring of clinical parameters and symptom patterns. It combines mobile accessibility, real-time decision logic, and clinical-grade risk assessment to support patients and caregivers.

---

## Project Overview

Pregaview enhances maternal care delivery through a modular, sensor-compatible platform composed of:
- A mobile-based patient interface (Android app)
- A Python Flask backend for clinical rule execution
- Modular risk classification models
- Firebase-based cloud infrastructure for data sync and alerts

The system aims to aid in the early identification of:
- **Preeclampsia**
- **Anemia**
- **Gestational Diabetes**
- **Normal pregnancy health status**

By capturing physiological trends and symptomatology, Pregaview assists in timely triage, improving maternal outcomes especially in underserved settings.

---

##  Key Capabilities

-  Risk assessment based on vitals (BP, glucose, heart rate) and clinical symptoms
-  Backend rule execution using pre-trained models
-  Mobile-first interface for easy patient engagement
-  Real-time sync, user auth, and alert delivery via Firebase
-  REST API for frontend-backend communication
-  Designed with patient data privacy and healthcare compliance in mind

---

## Tech Stack

| Module        | Technology                          |
|---------------|--------------------------------------|
| Mobile App    | Kotlin (Android), XML, Retrofit      |
| Backend       | Flask (Python 3.10), RESTful API     |
| Models        | Random Forest (.joblib), CNN (.tflite)|
| Cloud Layer   | Firebase Auth, Firestore, FCM        |
| Tools Used    | Android Studio, Postman, Jupyter     |

---

##  Installation Instructions

1. **Clone the Repository**
```bash
git clone https://github.com/your-username/pregaview.git
cd pregaview
