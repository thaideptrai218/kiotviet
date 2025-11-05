// static/script/product.js
let uploadedImages = [];

// Khi DOM load xong
document.addEventListener('DOMContentLoaded', function () {
    setupEventListeners();
});

function setupEventListeners() {
    // Bấm "Create product"
    // XÓA TOÀN BỘ ĐOẠN NÀY
    document.getElementById('createProductBtn').addEventListener('click', function (e) {
        openCreateModal();
    });

    // Tìm kiếm
    const searchInput = document.getElementById('globalSearch');
    searchInput.addEventListener('input', debounce(filterTable, 300));

    // Select all
    document.getElementById('selectAll').addEventListener('change', function () {
        document.querySelectorAll('#productTable tbody input[type="checkbox"]')
            .forEach(cb => cb.checked = this.checked);
    });

    // Upload ảnh
    setupImageUpload();
}

function openCreateModal() {
    resetForm();
    loadCategories();
    const modal = new bootstrap.Modal(document.getElementById('createProductModal'));
    modal.show();
}

function resetForm() {
    const form = document.getElementById('productForm');
    form.reset();
    document.getElementById('name').value = '';
    uploadedImages = [];
    document.getElementById('imagePreview').innerHTML = '';
}

async function loadCategories() {
    try {
        const res = await fetch('/api/categories');
        const categories = await res.json();
        const select = document.getElementById('categoryId');
        select.innerHTML = '<option>Select category (Required)</option>';
        categories.forEach(c => {
            select.innerHTML += `<option value="${c.id}">${c.name}</option>`;
        });
    } catch (err) {
        console.error('Load categories failed:', err);
    }
}

function setupImageUpload() {
    const dropzone = document.getElementById('imageDropzone');
    const input = document.getElementById('imageInput');

    dropzone.addEventListener('click', () => input.click());
    dropzone.addEventListener('dragover', e => { e.preventDefault(); dropzone.classList.add('border-primary'); });
    dropzone.addEventListener('dragleave', () => dropzone.classList.remove('border-primary'));
    dropzone.addEventListener('drop', e => {
        e.preventDefault();
        dropzone.classList.remove('border-primary');
        handleFiles(e.dataTransfer.files);
    });
    input.addEventListener('change', e => handleFiles(e.target.files));
}

function handleFiles(files) {
    [...files].forEach(file => {
        if (file.size > 2 * 1024 * 1024) {
            alert('Image must be under 2MB');
            return;
        }
        if (!file.type.startsWith('image/')) return;

        const reader = new FileReader();
        reader.onload = e => {
            uploadedImages.push(file);
            document.getElementById('imagePreview').innerHTML += `
                <div class="position-relative d-inline-block m-1">
                    <img src="${e.target.result}" class="img-thumbnail" width="80">
                    <button type="button" class="btn-close position-absolute top-0 end-0" 
                            onclick="this.parentElement.remove(); uploadedImages = uploadedImages.filter(f => f !== this.dataset.file)"></button>
                </div>`;
        };
        reader.readAsDataURL(file);
    });
}

// Lưu sản phẩm
async function saveProduct(andAnother = false) {
    const formData = new FormData();
    formData.append('name', document.getElementById('name').value);
    formData.append('barcode', document.getElementById('barcode').value);
    formData.append('categoryId', document.getElementById('categoryId').value);
    formData.append('costPrice', document.getElementById('costPrice').value);
    formData.append('sellingPrice', document.getElementById('sellingPrice').value);
    formData.append('onHand', document.getElementById('onHand').value);
    formData.append('minLevel', document.getElementById('minLevel').value);
    formData.append('maxLevel', document.getElementById('maxLevel').value);
    formData.append('forSale', document.getElementById('forSale').checked);

    uploadedImages.forEach(img => formData.append('images', img));

    try {
        const res = await fetch('/api/products', { method: 'POST', body: formData });
        if (res.ok) {
            alert('Product created successfully!');
            if (!andAnother) {
                bootstrap.Modal.getInstance(document.getElementById('createProductModal')).hide();
            } else {
                resetForm();
                loadCategories();
            }
        } else {
            const err = await res.json();
            alert(err.message || 'Error');
        }
    } catch (err) {
        alert('Network error');
    }
}

// Tìm kiếm client-side (tạm thời)
function filterTable() {
    const query = document.getElementById('globalSearch').value.toLowerCase();
    document.querySelectorAll('#productTable tbody tr').forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(query) ? '' : 'none';
    });
}

function debounce(func, wait) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

// Gọi khi mở modal Create Product → load danh sách category
async function loadCategories() {
    try {
        const res = await fetch('/api/categories');
        const categories = await res.json();

        // Cập nhật select trong modal Create Product
        const productSelect = document.getElementById('categoryId');
        productSelect.innerHTML = '<option value="">Select category (Required)</option>';
        
        // Cập nhật select trong filter
        const filterSelect = document.getElementById('filterCategory');
        filterSelect.innerHTML = '<option value="">Select category</option>';

        // Cập nhật select Parent trong modal Create Category
        const parentSelect = document.getElementById('parentCategoryId');
        parentSelect.innerHTML = '<option value="">Select parent category</option>';

        categories.forEach(c => {
            const opt = `<option value="${c.id}">${c.name}</option>`;
            productSelect.innerHTML += opt;
            filterSelect.innerHTML += opt;
            parentSelect.innerHTML += opt;
        });
    } catch (err) {
        console.error('Load categories failed:', err);
    }
}

// Lưu danh mục mới
async function saveCategory() {
    const name = document.getElementById('categoryName').value.trim();
    const parentId = document.getElementById('parentCategoryId').value || null;

    if (!name) {
        alert('Please enter category name!');
        return;
    }

    const data = { name, parentId };

    try {
        const res = await fetch('/api/categories', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            const newCat = await res.json();
            alert('Category created successfully!');

            // Đóng modal
            bootstrap.Modal.getInstance(document.getElementById('createCategoryModal')).hide();

            // Reset form
            document.getElementById('categoryForm').reset();

            // Tải lại danh sách
            await loadCategories();

            // Tự động chọn danh mục mới trong form Create Product
            document.getElementById('categoryId').value = newCat.id;

        } else {
            const err = await res.json();
            alert(err.message || 'Error creating category');
        }
    } catch (err) {
        alert('Network error');
    }
}