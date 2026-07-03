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


@app.get("/")
def home():
    return {
        "message": "AI Guardrail Fraud Detection API is running."
    }


@app.post("/simulate")
def simulate(tx: Transaction):

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

    # Final Decision
    if risk >= 0.80:
        return {
            "status": "BLOCKED",
            "risk_score": risk,
            "escrow": True,
            "screen_lock": 180,
            "message": (
                "Suspicious transaction detected. "
                "Funds moved to Hidden Escrow and device temporarily locked."
            )
        }

    elif risk >= 0.50:
        return {
            "status": "REVIEW",
            "risk_score": risk,
            "escrow": False,
            "screen_lock": 30,
            "message": (
                "Transaction requires additional verification before approval."
            )
        }

    return {
        "status": "SAFE",
        "risk_score": risk,
        "escrow": False,
        "screen_lock": 0,
        "message": "Transaction verified successfully."
    }