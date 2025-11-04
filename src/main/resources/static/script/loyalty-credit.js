// Simulated customer data for testing
const customer = {
    loyaltyPoints: {
        balance: 16596,
        history: [
            { type: 'earned', date: '2025-09-01', points: 500, description: 'Purchase at Store A' },
            { type: 'redeemed', date: '2025-09-10', points: 200, description: 'Used for Discount' },
            { type: 'earned', date: '2025-09-15', points: 700, description: 'Purchase at Store B' }
        ],
        tier: 'Gold',
        expiring: [
            { points: 200, expiryDate: '2025-11-01' }
        ]
    },
    creditStatus: {
        limit: 5000,
        balance: 1500,
        available: 3500,
        history: [
            { date: '2025-09-05', amount: 1000, status: 'Paid' },
            { date: '2025-09-15', amount: 500, status: 'Pending' }
        ]
    },
    communications: [
        { date: '2025-09-01', type: 'Email', details: 'Sent a promotional email' },
        { date: '2025-09-05', type: 'Call', details: 'Customer called regarding a product' }
    ]
};

// Function to load loyalty points status
function loadLoyalty() {
    document.getElementById("loyaltyBalance").textContent = customer.loyaltyPoints.balance;
    const nextTierProgress = customer.loyaltyPoints.balance / 20000 * 100; // Example calculation for progress
    document.getElementById("pointsProgress").style.width = `${nextTierProgress}%`;

    // Display next tier
    const nextTier = customer.loyaltyPoints.tier === 'Gold' ? 'Platinum' : 'Gold';
    document.getElementById("nextTier").textContent = nextTier;
}

// Function to load credit status
function loadCredit() {
    document.getElementById("creditLimit").textContent = customer.creditStatus.limit;
    document.getElementById("creditBalance").textContent = customer.creditStatus.balance;
    document.getElementById("availableCredit").textContent = customer.creditStatus.available;

    // Credit utilization
    const utilization = (customer.creditStatus.balance / customer.creditStatus.limit) * 100;
    document.getElementById("creditUtilization").style.width = `${utilization}%`;
    document.getElementById("utilizationPercentage").textContent = `${utilization.toFixed(2)}%`;
}

// Function to load payment history
function loadPaymentHistory() {
    const paymentHistory = customer.creditStatus.history.map(payment => `
        <tr>
            <td>${payment.date}</td>
            <td>$${payment.amount}</td>
            <td>${payment.status}</td>
        </tr>
    `).join('');
    document.getElementById("paymentHistory").innerHTML = paymentHistory;
}

// Function to load communication history
function loadCommunicationLog() {
    const communicationLog = customer.communications.map(comm => `
        <li>${comm.date} - ${comm.type}: ${comm.details}</li>
    `).join('');
    document.getElementById("communicationLog").innerHTML = communicationLog;
}

// Initialize page data
function init() {
    loadLoyalty();
    loadCredit();
    loadPaymentHistory();
    loadCommunicationLog();
}

init();
