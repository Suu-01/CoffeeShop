let categoryModal;

document.addEventListener('DOMContentLoaded', function() {
    categoryModal = new bootstrap.Modal(document.getElementById('productModal'));
    loadCategorys();
});

async function loadCategorys() {
    try {
        const response = await fetch('/api/categorys');
        const products = await response.json();
        
        const tbody = document.getElementById('productTableBody');
        tbody.innerHTML = '';
        
        products.forEach(product => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${product.id}</td>
                <td>${product.name}</td>
                <td>${product.description || ''}</td>
                <td>${product.postCount}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="editCategory(${product.id})">수정</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteCategory(${product.id})">삭제</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('카테고리 목록을 불러오는데 실패했습니다:', error);
        alert('카테고리 목록을 불러오는데 실패했습니다.');
    }
}

function showAddCategoryModal() {
    document.getElementById('modalTitle').textContent = '카테고리 추가';
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    categoryModal.show();
}

async function editCategory(id) {
    try {
        const response = await fetch(`/api/categorys/${id}`);
        const product = await response.json();
        
        document.getElementById('modalTitle').textContent = '카테고리 수정';
        document.getElementById('productId').value = product.id;
        document.getElementById('productName').value = product.name;
        document.getElementById('productDescription').value = product.description || '';
        const countInput = document.getElementById('postCount');
        if (countInput) countInput.value = product.postCount ?? 0;
        
        categoryModal.show();
    } catch (error) {
        console.error('카테고리 정보를 불러오는데 실패했습니다:', error);
        alert('카테고리 정보를 불러오는데 실패했습니다.');
    }
}

async function saveCategory() {
    const id = document.getElementById('productId').value;
    const product = {
        name: document.getElementById('productName').value,
        description: document.getElementById('productDescription').value,
        // postCount는 서버에서 관리하므로 전송하지 않음
    };

    try {
        const url = id ? `/api/categorys/${id}` : '/api/categorys';
        const method = id ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(product)
        });

        if (!response.ok) {
            throw new Error('저장에 실패했습니다.');
        }

        categoryModal.hide();
        loadCategorys();
        alert('저장되었습니다.');
    } catch (error) {
        console.error('저장에 실패했습니다:', error);
        alert('저장에 실패했습니다.');
    }
}

async function deleteCategory(id) {
    if (!confirm('정말 삭제하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/api/categorys/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('삭제에 실패했습니다.');
        }

        loadCategorys();
        alert('삭제되었습니다.');
    } catch (error) {
        console.error('삭제에 실패했습니다:', error);
        alert('삭제에 실패했습니다.');
    }
} 