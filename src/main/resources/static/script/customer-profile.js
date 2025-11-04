// Customer data simulation for testing
const customer = {
    id: 1,
    code: "KH123456",
    name: "Nguyen Van Hai",
    phone: "0987123456",
    email: "hai@example.com",
    address: "123 Nguyen Trai, Hanoi",
    status: "Active",
    notes: "VIP customer",
    tags: ["VIP", "Regular"],
    orders: [
        { code: "ORD001", date: "2025-10-01", total: 450000, status: "Completed" },
        { code: "ORD002", date: "2025-10-10", total: 320000, status: "Completed" },
        { code: "ORD003", date: "2025-11-01", total: 200000, status: "Pending" }
    ]
};

// Function to generate a customer code (just for testing)
function generateCode() { return "KH" + String(Math.floor(100000 + Math.random() * 900000)); }

// Load customer data into the form
function loadProfile() {
    customer.code = customer.code || generateCode();
    document.getElementById("customerCode").value = customer.code;
    document.getElementById("name").value = customer.name;
    document.getElementById("phone").value = customer.phone;
    document.getElementById("email").value = customer.email;
    document.getElementById("address").value = customer.address;
    document.getElementById("status").value = customer.status;
    document.getElementById("notes").value = customer.notes;
    renderTags();
    renderOrders();
}

// Render tags dynamically
function renderTags() {
    const tagList = document.getElementById("tagList");
    tagList.innerHTML = customer.tags.map(t => `
        <div class="tag">
            ${t}
            <span data-tag="${t}">&times;</span>
        </div>
    `).join("");
}

// Render order history dynamically
function renderOrders() {
    document.getElementById("orderHistory").innerHTML = customer.orders.map(o => `
        <tr>
            <td>${o.code}</td>
            <td>${o.date}</td>
            <td>${o.total.toLocaleString()}</td>
            <td>${o.status}</td>
            <td><button class="btn btn-sm btn-outline-primary">View</button></td>
        </tr>
    `).join("");
}

// Add a tag to the profile
document.getElementById("addTag").onclick = () => {
    const tag = document.getElementById("tagInput").value.trim();
    if (tag && !customer.tags.includes(tag)) {
        customer.tags.push(tag);
        document.getElementById("tagInput").value = "";
        renderTags();
    }

    // Remove a tag from the profile
    document.getElementById("tagList").onclick = e => {
        if (e.target.dataset.tag) {
            customer.tags = customer.tags.filter(t => t !== e.target.dataset.tag);
            renderTags();
        }
    };

    // Avatar upload handling
    document.getElementById("avatarUpload").onchange = e => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = () => {
                document.getElementById("avatarPreview").src = reader.result;
            };
            reader.readAsDataURL(file);
        }
    };

    // Save the changes to customer data
    document.getElementById("saveBtn").onclick = () => {
        customer.name = document.getElementById("name").value;
        customer.phone = document.getElementById("phone").value;
        customer.email = document.getElementById("email").value;
        customer.address = document.getElementById("address").value;
        customer.status = document.getElementById("status").value;
        customer.notes = document.getElementById("notes").value;

        alert("Profile saved successfully!");
    };

    // Reset the form to the initial state
    document.getElementById("resetBtn").onclick = () => loadProfile();
};

// Initial load when the page is ready
loadProfile();
