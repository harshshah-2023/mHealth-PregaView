# 🌸 Pregaview

### *A Multi-Parametric Decision Support System for Maternal Health Monitoring*

![Status](https://img.shields.io/badge/Status-Active-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)
![Platform](https://img.shields.io/badge/Platform-Android-orange)
![Backend](https://img.shields.io/badge/Backend-Flask-red)

---

 ## 📌 Overview


**Pregaview** is a smart maternal healthcare decision-support system designed to **detect early risks in pregnancy** through continuous monitoring of physiological data and clinical symptoms.

It helps in the early detection of:

* **Preeclampsia**
* **Anemia**
* **Gestational Diabetes**
* **Normal pregnancy health status**

The solution bridges the gap in maternal care, especially in low-resource settings, by combining:

✔ Real-time decision logic
✔ Mobile accessibility
✔ Clinical-grade risk classification
✔ Remote monitoring

---
---
❗ Problem Statement
---

Maternal deaths continue to rise due to late diagnosis of pregnancy-related complications.
Many expecting mothers lack:

Continuous monitoring

Timely access to hospitals

Awareness of early warning symptoms

Pregaview helps solve this by enabling early identification and real-time decision support, improving clinical outcomes and reducing emergency situations.

---
---

## 🚀 System Architecture

Pregaview is composed of:

* 📱 **Android Mobile App (Frontend)**
* 🧠 **Python Flask Backend for rule processing**
* 🌩 **Firebase Cloud for authentication, alerts & data storage**
* 🔍 **Machine learning models for risk prediction**

---

## 🌟 Key Features

* 🔄 Real-time vitals and symptom monitoring
* ⚕ AI-based risk classification
* ☁ Secure cloud sync using Firebase
* 📬 Push alerts for high-risk cases
* 🔐 Healthcare data privacy in design
* 🔗 REST API communication between app and backend

---

## 🧠 Tech Stack

| Component      | Technology Used                            |
| -------------- | ------------------------------------------ |
| Frontend       | Kotlin (Android), XML, Retrofit            |
| Backend        | Python 3.10, Flask (REST API)              |
| ML Models      | Random Forest (`.joblib`), CNN (`.tflite`) |
| Cloud Services | Firebase Auth, Firestore, FCM              |
| Tools          | Android Studio, Jupyter, Postman           |

---

## 📡 High-Level Workflow

```
[User Vitals & Symptoms] 
          ↓  
  [Android Application]
          ↓  
      [Flask Backend]
          ↓  
[ML Models & Decision Rules]
          ↓  
      [Risk Assessment]
          ↓  
  [Firebase Alerts & Monitoring]
```

---
---

## 📊 Results & Impact

Predicts pregnancy risks using 14+ clinical parameters


Helps in reducing emergency referrals


Designed for rural and remote monitoring


Enables structured triaging and prioritization


Improves clinical decision-making for healthcare workers

---

## 🔮 Future Enhancements

Integration with wearable IoT sensors

Tele-consultation support

Backend deployment on AWS / Azure

Addition of more AI/ML risk models

Multilingual support for broader usage

Advanced dashboards for doctors & caregivers

## 💻 Installation & Setup

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/pregaview.git
cd pregaview
```

### 2️⃣ Setup Backend

```bash
pip install -r requirements.txt
python app.py
```

### 3️⃣ Configure Android App

* Open the project in **Android Studio**
* Update `google-services.json`
* Add backend API URL in Retrofit configuration

---

## 📱 Screenshots


![WhatsApp Image 2025-11-15 at 11 21 47 AM](https://github.com/user-attachments/assets/b35d00b1-c006-4367-bb07-2bf326cf7ebe)
![WhatsApp Image 2025-11-15 at 11 21 47 AM (1)](https://github.com/user-attachments/assets/ed7a9e14-3a84-4aa9-9f7a-e8d61025ea5c)
![WhatsApp Image 2025-11-15 at 11 24 01 AM](https://github.com/user-attachments/assets/320b19f4-de7c-410e-88ac-728321c32ed1)
![WhatsApp Image 2025-11-15 at 11 21 49 AM (1)](https://github.com/user-attachments/assets/642e92cf-b61d-4394-a130-f30f624e2f2b)
![WhatsApp Image 2025-11-15 at 11 21 48 AM](https://github.com/user-attachments/assets/38bfe131-4455-44a0-a617-7020ca5ca77d)
![WhatsApp Image 2025-11-15 at 11 21 48 AM (1)](https://github.com/user-attachments/assets/d9b17add-a119-41b2-819d-4fc5a054e2b1)




---

## 🤝 Contributors

| Name      | Role                              |
| --------- | --------------------------------- |
| Your Name | Lead Developer & System Architect |

---

