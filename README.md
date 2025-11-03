
![WhatsApp Image 2025-11-03 at 4 21 43 PM](https://github.com/user-attachments/assets/a46c235d-1a46-499e-bcfd-c9bd3f5014e7)
![WhatsApp Image 2025-11-03 at 4 21 43 PM (1)](https://github.com/user-attachments/assets/560ed46d-53bb-4074-97a1-01550a15710f)


🔐 Role-Based Access Control:
Dynamically determines which modules the user can access based on their role and permissions.

🧊 Cooling Period Lockout:
Restricts user access for a defined duration (between coolingStartTime and coolingEndTime).
Displays a live countdown (e.g., “Cooling ends in 02:14”).

⚙️ Reusable AccessManager Component:
Central logic that:

Checks if the user is in a cooling period

Verifies if a module is accessible

Returns formatted countdown string

🧱 Dynamic Module Rendering:
UI modules are populated dynamically from mock JSON data (Payments, Account Info, Consent Center).

💬 User Feedback:
Each module tap shows a contextual message:

✅ “Navigating to Payments” (if accessible)

🚫 “Access denied: cooling period”

🚫 “Access denied: no permission”
