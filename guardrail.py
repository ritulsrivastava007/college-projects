from fastapi import FastAPI
from pydantic import BaseModel
import random

app = FastAPI(title="AI Guardrail Fraud Detection API")


class Transaction(BaseModel):
    sender: str
    amount: int


# Known fraudulent senders
BLACKLIST = {
    "Scammer123",
    "FraudBank",
    "UnknownXYZ"
}

# Stores latest simulation state
state = {
    "transaction": {},
    "ai_engine": {},
    "ledger": {}
}


@app.get("/")
def home():
    return {
        "message": "AI Guardrail Fraud Detection API is running."
    }


@app.post("/reset")
def reset():
    global state

    state = {
        "transaction": {},
        "ai_engine": {},
        "ledger": {}
    }

    return {"message": "Simulation reset"}


@app.get("/status")
def status():
    return state


@app.post("/simulate")
def simulate(tx: Transaction):
    global state

    # Rule 1: Blacklisted sender -> Very High Risk
    if tx.sender in BLACKLIST:
        risk = 0.95

    # Rule 2: Very large transaction -> High Risk
    elif tx.amount >= 100000:
        risk = round(random.uniform(0.82, 0.92), 2)

    # Rule 3: Medium transaction -> Moderate Risk
    elif tx.amount >= 50000:
        risk = round(random.uniform(0.55, 0.79), 2)

    # Rule 4: Small transaction -> Low Risk
    else:
        risk = round(random.uniform(0.10, 0.50), 2)

    blacklist_hit = tx.sender in BLACKLIST
    screen_share_detected = risk >= 0.80
    typing_anomaly = risk >= 0.80

    # Decide final status
    if risk >= 0.80:
        decision = "Blocked"
        status = "BLOCKED"
        escrow = True
        screen_lock = 180
        message = (
            "Suspicious transaction detected. "
            "Funds moved to Hidden Escrow and device temporarily locked."
        )

    elif risk >= 0.50:
        decision = "Review"
        status = "REVIEW"
        escrow = False
        screen_lock = 30
        message = (
            "Transaction requires additional verification before approval."
        )

    else:
        decision = "Approved"
        status = "SAFE"
        escrow = False
        screen_lock = 0
        message = "Transaction verified successfully."

    balance = 142500

    if decision == "Approved":
        balance += tx.amount

    state["transaction"] = {
        "sender_name": tx.sender,
        "amount": tx.amount
    }

    state["ai_engine"] = {
        "ai_status": "Finished",
        "risk_score": int(risk * 100),
        "sender_risk": "HIGH" if blacklist_hit else "LOW",
        "device_risk": "RISK" if screen_share_detected else "SAFE",
        "typing_anomaly": typing_anomaly,
        "blacklist_hit": blacklist_hit,
        "screen_share_detected": screen_share_detected,
        "final_decision": decision
    }

    state["ledger"] = {
        "displayed_balance": balance
    }

    return {
        "status": status,
        "risk_score": risk,
        "escrow": escrow,
        "screen_lock": screen_lock,
        "message": message,
        "transaction": state["transaction"],
        "ai_engine": state["ai_engine"],
        "ledger": state["ledger"]
    }
