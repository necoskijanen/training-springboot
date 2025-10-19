document.addEventListener('DOMContentLoaded', function () {
    initializeSearch();
});

function initializeSearch() {
    const searchBtn = document.getElementById('searchBtn');
    const clearBtn = document.getElementById('clearBtn');

    searchBtn.addEventListener('click', performSearch);
    clearBtn.addEventListener('click', clearForm);

    // Check if user is admin
    checkAdminStatus();

    // Load users list for admin
    loadUsersList();

    // 初期表示：ページ読み込み時に直近1ページ分を自動検索
    performSearch();
}

function checkAdminStatus() {
    const userId = document.getElementById('userId');
    const userHeaderCell = document.getElementById('userHeaderCell');

    // Check current URL path to determine role
    const path = window.location.pathname;
    const isAdmin = path.includes('/admin/');

    if (isAdmin && userId) {
        // Show the user selection field by removing admin-only class restriction
        const parentGroup = userId.parentElement;
        if (parentGroup && parentGroup.classList.contains('admin-only')) {
            parentGroup.classList.add('visible');
        }
        userHeaderCell.style.display = 'table-cell';
    }
}

function loadUsersList() {
    const path = window.location.pathname;
    const isAdmin = path.includes('/admin/');

    if (!isAdmin) {
        return;
    }

    // Get users from user search API or create endpoint
    // For now, we'll leave it as a placeholder
    // Admin can manually enter user ID or we can add a separate API call
}

function performSearch() {
    const jobName = document.getElementById('jobName').value.trim();
    const status = document.getElementById('status').value;
    const startDateFrom = document.getElementById('startDateFrom').value;
    const endDateTo = document.getElementById('endDateTo').value;
    const userId = document.getElementById('userId').value;

    showLoading(true);
    hideError();

    // Build query parameters
    const params = new URLSearchParams();

    if (jobName) {
        params.append('jobName', jobName);
    }
    if (status) {
        params.append('status', status);
    }
    if (startDateFrom) {
        params.append('startDateFrom', startDateFrom);
    }
    if (endDateTo) {
        params.append('endDateTo', endDateTo);
    }
    if (userId) {
        params.append('userId', userId);
    }

    params.append('page', 0);
    params.append('size', 10);

    fetch(`/api/batch/history/search?${params.toString()}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            displayResults(data);
            showLoading(false);
        })
        .catch(error => {
            console.error('Search error:', error);
            showError('Failed to search batch history. Please try again.');
            showLoading(false);
        });
}

function displayResults(data) {
    const resultsContainer = document.getElementById('resultsContainer');
    const noResults = document.getElementById('noResults');
    const resultsInfo = document.getElementById('resultsInfo');
    const resultsTableBody = document.getElementById('resultsTableBody');
    const currentPageSpan = document.getElementById('currentPage');
    const totalPagesSpan = document.getElementById('totalPages');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    // Clear previous results
    resultsTableBody.innerHTML = '';

    const totalCount = data.totalCount || 0;
    const totalPages = data.totalPages || 1;
    const currentPage = data.currentPage || 1;

    if (totalCount === 0) {
        resultsContainer.style.display = 'none';
        noResults.style.display = 'block';
        return;
    }

    // Display results info
    resultsInfo.textContent = `${totalCount}件の検索結果`;

    // Populate table
    const results = data.content || [];
    results.forEach(item => {
        const row = document.createElement('tr');

        // User name column (admin only) - placed first
        const userHeaderCell = document.getElementById('userHeaderCell');
        if (userHeaderCell.style.display !== 'none') {
            const userCell = document.createElement('td');
            userCell.textContent = item.userName || '';
            row.appendChild(userCell);
        }

        const executionIdCell = document.createElement('td');
        executionIdCell.textContent = item.executionId ? item.executionId.substring(0, 8) + '...' : '';
        row.appendChild(executionIdCell);

        const jobNameCell = document.createElement('td');
        jobNameCell.textContent = item.jobName || '';
        row.appendChild(jobNameCell);

        const startTimeCell = document.createElement('td');
        startTimeCell.textContent = formatDateTime(item.startTime);
        row.appendChild(startTimeCell);

        const endTimeCell = document.createElement('td');
        endTimeCell.textContent = formatDateTime(item.endTime);
        row.appendChild(endTimeCell);

        const statusCell = document.createElement('td');
        statusCell.appendChild(createStatusBadge(item.status));
        row.appendChild(statusCell);

        const exitCodeCell = document.createElement('td');
        exitCodeCell.textContent = item.exitCode !== null ? item.exitCode : '-';
        row.appendChild(exitCodeCell);

        resultsTableBody.appendChild(row);
    });

    // Update pagination
    currentPageSpan.textContent = currentPage;
    totalPagesSpan.textContent = totalPages;

    prevBtn.disabled = currentPage <= 1;
    nextBtn.disabled = currentPage >= totalPages;

    prevBtn.onclick = () => goToPage(currentPage - 1);
    nextBtn.onclick = () => goToPage(currentPage + 1);

    resultsContainer.style.display = 'block';
    noResults.style.display = 'none';
}

function goToPage(pageNumber) {
    const jobName = document.getElementById('jobName').value.trim();
    const status = document.getElementById('status').value;
    const startDateFrom = document.getElementById('startDateFrom').value;
    const endDateTo = document.getElementById('endDateTo').value;
    const userId = document.getElementById('userId').value;

    showLoading(true);
    hideError();

    // Build query parameters
    const params = new URLSearchParams();

    if (jobName) {
        params.append('jobName', jobName);
    }
    if (status) {
        params.append('status', status);
    }
    if (startDateFrom) {
        params.append('startDateFrom', startDateFrom);
    }
    if (endDateTo) {
        params.append('endDateTo', endDateTo);
    }
    if (userId) {
        params.append('userId', userId);
    }

    params.append('page', pageNumber - 1);
    params.append('size', 10);

    fetch(`/api/batch/history/search?${params.toString()}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            displayResults(data);
            showLoading(false);
        })
        .catch(error => {
            console.error('Search error:', error);
            showError('ページの読み込みに失敗しました。もう一度お試しください。');
            showLoading(false);
        });
}

function createStatusBadge(status) {
    const badge = document.createElement('span');
    badge.classList.add('status-badge');

    let displayText = '';
    let className = '';

    switch (status) {
        case 'RUNNING':
            displayText = '実行中';
            className = 'status-running';
            break;
        case 'COMPLETED_SUCCESS':
            displayText = '完了（成功）';
            className = 'status-success';
            break;
        case 'FAILED':
            displayText = '失敗';
            className = 'status-failed';
            break;
        default:
            displayText = status;
    }

    badge.textContent = displayText;
    badge.classList.add(className);

    return badge;
}

function formatDateTime(dateTimeString) {
    if (!dateTimeString) {
        return '-';
    }

    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString('ja-JP', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    } catch (e) {
        return dateTimeString;
    }
}

function clearForm() {
    document.getElementById('jobName').value = '';
    document.getElementById('status').value = '';
    document.getElementById('startDateFrom').value = '';
    document.getElementById('endDateTo').value = '';
    document.getElementById('userId').value = '';

    document.getElementById('resultsContainer').style.display = 'none';
    document.getElementById('noResults').style.display = 'none';
    hideError();
}

function showError(message) {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.textContent = message;
    errorMessage.style.display = 'block';
}

function hideError() {
    const errorMessage = document.getElementById('errorMessage');
    errorMessage.style.display = 'none';
}

function showLoading(show) {
    const loading = document.getElementById('loading');
    loading.style.display = show ? 'block' : 'none';
}
