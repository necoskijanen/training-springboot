let selectedJob = null;
let currentExecutionId = null;
let currentPage = 0;
const pageSize = 10;
let totalPages = 1;
let pollingInterval = null;
const csrfToken = document.querySelector('meta[name="csrf-token"]')?.content;
const csrfHeader = document.querySelector('meta[name="csrf-header"]')?.content || 'X-CSRF-TOKEN';

// ページ読み込み時の処理
window.addEventListener('DOMContentLoaded', function () {
    loadAvailableJobs();
    loadHistory(0);
});

function getHeaders() {
    return {
        'Content-Type': 'application/json',
        // 2. CSRFトークンをヘッダーに含める
        'X-CSRF-TOKEN': csrfToken
    };
}

/**
 * 利用可能なジョブを読み込む
 */
function loadAvailableJobs() {
    fetch('/api/batch/jobs')
        .then(response => response.json())
        .then(jobs => {
            const jobSelect = document.getElementById('jobSelect');
            jobs.forEach(job => {
                const option = document.createElement('option');
                option.value = job.id;
                option.textContent = job.name;
                jobSelect.appendChild(option);
            });
        })
        .catch(error => {
            showAlert('ジョブの読み込みに失敗しました: ' + error.message, 'error');
            console.error('Error loading jobs:', error);
        });
}

/**
 * ジョブが選択された時の処理
 */
function onJobChanged() {
    const jobSelect = document.getElementById('jobSelect');
    const selectedJobId = jobSelect.value;

    if (!selectedJobId) {
        selectedJob = null;
        clearParameters();
        document.getElementById('executeBtn').disabled = true;
        return;
    }

    // 選択したジョブの情報を取得
    fetch('/api/batch/jobs')
        .then(response => response.json())
        .then(jobs => {
            selectedJob = jobs.find(j => j.id === selectedJobId);
            if (selectedJob) {
                displayJobParameters();
                document.getElementById('executeBtn').disabled = false;
            }
        })
        .catch(error => {
            showAlert('ジョブ情報の取得に失敗しました', 'error');
            console.error('Error loading job details:', error);
        });
}

/**
 * ジョブパラメータを表示する
 */
function displayJobParameters() {
    if (!selectedJob) return;

    const tbody = document.querySelector('#parametersTable tbody');
    tbody.innerHTML = '';

    // 基本情報
    const rows = [
        { name: 'ジョブID', value: selectedJob.id },
        { name: 'ジョブ名', value: selectedJob.name },
        { name: '説明', value: selectedJob.description },
        { name: 'コマンド', value: selectedJob.command },
        {
            name: '引数',
            value: selectedJob.arguments ? selectedJob.arguments.join(' ') : '（なし）'
        },
        { name: 'タイムアウト', value: selectedJob.timeout + '秒' }
    ];

    rows.forEach(row => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${row.name}</td><td>${row.value}</td>`;
        tbody.appendChild(tr);
    });
}

/**
 * パラメータ表示をクリア
 */
function clearParameters() {
    const tbody = document.querySelector('#parametersTable tbody');
    tbody.innerHTML = '<tr><td colspan="2" style="text-align: center; color: #999;">ジョブを選択してください</td></tr>';
}

/**
 * ジョブ詳細を表示
 */
function showJobDetails() {
    if (!selectedJob) {
        showAlert('ジョブを選択してください', 'info');
        return;
    }

    let details = `ジョブID: ${selectedJob.id}\n`;
    details += `ジョブ名: ${selectedJob.name}\n`;
    details += `説明: ${selectedJob.description}\n`;
    details += `コマンド: ${selectedJob.command}\n`;
    details += `引数: ${selectedJob.arguments ? selectedJob.arguments.join(' ') : '（なし）'}\n`;
    details += `タイムアウト: ${selectedJob.timeout}秒`;

    alert(details);
}

/**
 * バッチを実行する
 */
function executeBatch() {
    if (!selectedJob) {
        showAlert('ジョブを選択してください', 'info');
        return;
    }

    const executeBtn = document.getElementById('executeBtn');
    executeBtn.disabled = true;
    executeBtn.innerHTML = '<span class="loading"></span> 実行中...';
    let headers = getHeaders();

    fetch('/api/batch/execute?jobId=' + selectedJob.id, {
        method: 'POST',
        headers
    })
        .then(response => response.json())
        .then(data => {
            if (data.executionId) {
                currentExecutionId = data.executionId;
                showAlert('バッチを実行しました: ' + data.executionId, 'success');
                startPolling();
            } else {
                showAlert('バッチの実行に失敗しました', 'error');
                executeBtn.disabled = false;
                executeBtn.innerHTML = '実行';
            }
        })
        .catch(error => {
            showAlert('エラー: ' + error.message, 'error');
            executeBtn.disabled = false;
            executeBtn.innerHTML = '実行';
            console.error('Error executing batch:', error);
        });
}

/**
 * ステータスをポーリングする
 */
function startPolling() {
    // 既に実行中のポーリングをクリア
    if (pollingInterval) {
        clearInterval(pollingInterval);
    }

    // 5秒ごとにステータスをチェック
    pollingInterval = setInterval(() => {
        checkStatus();
    }, 5000);

    // 初回はすぐにチェック
    checkStatus();
}

/**
 * 実行ステータスをチェック
 */
function checkStatus() {
    if (!currentExecutionId) return;

    fetch('/api/batch/status/' + currentExecutionId)
        .then(response => response.json())
        .then(status => {
            console.log('Status:', status);

            // ステータスが完了またはエラーなら、ポーリングを停止
            if (status.status !== 'RUNNING') {
                clearInterval(pollingInterval);
                pollingInterval = null;

                const executeBtn = document.getElementById('executeBtn');
                executeBtn.disabled = false;
                executeBtn.innerHTML = '実行';

                if (status.status === 'COMPLETED_SUCCESS') {
                    showAlert('バッチ実行が完了しました', 'success');
                } else if (status.status === 'FAILED') {
                    showAlert('バッチ実行に失敗しました（終了コード: ' + status.exitCode + '）', 'error');
                }

                // 履歴を更新
                loadHistory(0);
            }
        })
        .catch(error => {
            console.error('Error checking status:', error);
        });
}

/**
 * 実行履歴を読み込む
 */
function loadHistory(page) {
    if (page < 0 || page >= totalPages) return;

    currentPage = page;

    fetch(`/api/batch/history?page=${page}&size=${pageSize}`)
        .then(response => response.json())
        .then(data => {
            totalPages = data.totalPages;
            displayHistory(data.content);
            updatePagination(data.currentPage, data.totalPages);
        })
        .catch(error => {
            showAlert('履歴の読み込みに失敗しました', 'error');
            console.error('Error loading history:', error);
        });
}

/**
 * 実行履歴を表示
 */
function displayHistory(executions) {
    const tbody = document.querySelector('#historyTable tbody');
    tbody.innerHTML = '';

    if (executions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #999;">履歴はまだありません</td></tr>';
        return;
    }

    executions.forEach(execution => {
        const tr = document.createElement('tr');
        const statusBadge = getStatusBadge(execution.status);
        const endTime = execution.endTime ? formatDateTime(execution.endTime) : '（実行中）';
        const exitCode = execution.exitCode !== null ? execution.exitCode : '-';

        tr.innerHTML = `
            <td>${execution.id.substring(0, 8)}...</td>
            <td>${execution.jobName}</td>
            <td>${formatDateTime(execution.startTime)}</td>
            <td>${endTime}</td>
            <td>${statusBadge}</td>
            <td>${exitCode}</td>
        `;
        tbody.appendChild(tr);
    });
}

/**
 * ステータスバッジを取得
 */
function getStatusBadge(status) {
    let className = '';
    let displayText = status;

    if (status === 'RUNNING') {
        className = 'status-running';
        displayText = '実行中';
    } else if (status === 'COMPLETED_SUCCESS') {
        className = 'status-success';
        displayText = '完了（成功）';
    } else if (status === 'FAILED') {
        className = 'status-failed';
        displayText = '失敗';
    }

    return `<span class="status-badge ${className}">${displayText}</span>`;
}

/**
 * ページネーションを更新
 */
function updatePagination(currentPageNum, totalPageNum) {
    const pageInfo = document.getElementById('pageInfo');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    pageInfo.textContent = `ページ: ${currentPageNum + 1} / ${totalPageNum}`;
    prevBtn.disabled = currentPageNum === 0;
    nextBtn.disabled = currentPageNum >= totalPageNum - 1;
}

/**
 * アラートを表示
 */
function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type}`;
    alertDiv.textContent = message;

    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertDiv);

    // 5秒後に消す
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

/**
 * 日時をフォーマット
 */
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return '-';
    const date = new Date(dateTimeString);
    return date.toLocaleString('ja-JP', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}
