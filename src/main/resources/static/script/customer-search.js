const customers = [
    { code: "KH001", name: "Nguyen Van Hai", phone: "0987123456", email: "hai@example.com", status: "Active", tier: "Gold", points: 16596 },
    { code: "KH002", name: "Pham Thu Huong", phone: "0912123456", email: "huong@example.com", status: "Active", tier: "Silver", points: 14550 },
    { code: "KH003", name: "Tuan Ha Noi", phone: "0905123456", email: "tuan@example.com", status: "Active", tier: "Gold", points: 6036 },
    { code: "KH004", name: "Anh Hoang Sai Gon", phone: "0978123456", email: "hoang@example.com", status: "Inactive", tier: "Bronze", points: 10692 },
    { code: "KH005", name: "Anh Giang Kim Ma", phone: "0989001122", email: "giang@example.com", status: "Active", tier: "Silver", points: 54501 }
];

const tableBody = document.getElementById("customer-table-body");
const totalCount = document.getElementById("total-count");
const activeCount = document.getElementById("active-count");
const inactiveCount = document.getElementById("inactive-count");
const newCount = document.getElementById("new-count");

function escapeRegex(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function renderTable(data, keyword = "") {
    const regex = new RegExp(`(${escapeRegex(keyword)})`, "gi");
    tableBody.innerHTML = data.map(c => {
        const code = keyword ? c.code.replace(regex, '<mark>$1</mark>') : c.code;
        const name = keyword ? c.name.replace(regex, '<mark>$1</mark>') : c.name;
        const phone = keyword ? c.phone.replace(regex, '<mark>$1</mark>') : c.phone;
        return `
        <tr>
          <td><input type="checkbox"></td>
          <td>${code}</td>
          <td>${name}</td>
          <td>${phone}</td>
          <td>${c.email}</td>
          <td><span class="badge ${c.status === "Active" ? "bg-success" : "bg-danger"}">${c.status}</span></td>
          <td>${c.tier}</td>
          <td>${c.points.toLocaleString()}</td>
          <td>
            <button class="btn btn-sm btn-outline-info btn-view">View Loyalty</button>
            <button class="btn btn-sm btn-outline-primary btn-edit">Edit Profile</button>
            </td>
          
        </tr>
      `;
    }).join("");
}

function updateStatsGlobal() {
    totalCount.textContent = customers.length;
    activeCount.textContent = customers.filter(c => c.status === "Active").length;
    inactiveCount.textContent = customers.filter(c => c.status === "Inactive").length;
    newCount.textContent = Math.floor(Math.random() * 3);
}

function applyFilters() {
    const kw = document.getElementById("search-input").value.toLowerCase();
    const status = document.getElementById("status-filter").value;
    const tier = document.getElementById("membership-filter").value;
    const sortKey = document.getElementById("sort-select").value;

    let filtered = customers.filter(c => {
        const matchKw = !kw || c.name.toLowerCase().includes(kw) || c.phone.includes(kw) || c.code.toLowerCase().includes(kw);
        const matchStatus = !status || c.status === status;
        const matchTier = !tier || c.tier === tier;
        return matchKw && matchStatus && matchTier;
    });

    filtered.sort((a, b) => {
        if (sortKey === "points") return b.points - a.points;
        return a[sortKey].localeCompare(b[sortKey], undefined, { sensitivity: 'base' });
    });

    renderTable(filtered, kw);
}

document.getElementById("apply-filters").addEventListener("click", applyFilters);

document.getElementById("clear-filters").addEventListener("click", () => {
    document.getElementById("search-input").value = "";
    document.getElementById("status-filter").value = "";
    document.getElementById("membership-filter").value = "";
    renderTable(customers);
});

document.getElementById("sort-select").addEventListener("change", applyFilters);

document.getElementById("select-all").addEventListener("change", (e) => {
    const checkboxes = document.querySelectorAll("#customer-table-body input[type='checkbox']");
    checkboxes.forEach(ch => ch.checked = e.target.checked);
});

renderTable(customers);
updateStatsGlobal();

document.getElementById("customer-table-body").addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-view")) {
        const row = e.target.closest("tr");
        const code = row.children[1].textContent;
        window.location.href = `loyalty-credit.html?code=${encodeURIComponent(code)}`;
    }
    if (e.target.classList.contains("btn-edit")) {
        const row = e.target.closest("tr");
        const code = row.children[1].textContent;
        window.location.href = `customer-profile.html?code=${encodeURIComponent(code)}&edit=true`;
    }
});

