$(document).ready(function () {
    blockBy('.count-statistic');
    $.ajax({
        url: 'statistic/get-general-statistic',
        type: 'get',
        success: function (response) {
            fillGeneralStatistic(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
            console.log(error);
        }
    }).done(function () {
        unblockBy('.count-statistic');
    });

    blockBy('#account-statistic')
    $.ajax({
        url: 'statistic/get-accounts-statistic',
        type: 'get',
        success: function (response) {
            fillPersonalAccountsStatistic(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
            console.log(error);
        }
    }).done(function () {
        unblockBy('#account-statistic')
    });

    blockBy('#income-expense-statistic')
    $.ajax({
        url: 'statistic/get-income-expense-statistic',
        type: 'get',
        success: function (response) {
            drawIncomeExpenseStatistic(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
            console.log(error);
        }
    }).done(function () {
        unblockBy('#income-expense-statistic')
    });

    blockBy('#arrears-paid-arrears-chart')
    $.ajax({
        url: 'statistic/get-paid-arrears-statistic',
        type: 'get',
        success: function (response) {
            console.log(response);
            drawPaidArrearsInvoicesStatistic(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
            console.log(error);
        }
    }).done(function () {
        unblockBy('#arrears-paid-arrears-chart')
    });
});

function fillGeneralStatistic(response) {
    $('#count-houses').html(response.countHouses).hide().show('');
    $('#count-apartments').html(response.countApartments).hide().show('');
    $('#count-apartment-owners').html(response.countActiveApartmentOwners).hide().show('');
    $('#count-personal-accounts').html(response.countPersonalAccounts).hide().show('');
    $('#count-master-request-in-progress').html(response.countMasterRequestsInProgress).hide().show('');
    $('#count-new-master-requests').html(response.countMasterRequestsNew).hide().show('');
}

function fillPersonalAccountsStatistic(response) {

    $('#account-balance-arrears').html(`${response.accountsBalanceArrears} ${currency}`).hide().show('');
    $('#account-current-balance').html(`${response.accountsBalanceOverpayments} ${currency}`).hide().show('');
    $('#account-cash-register-balance').html(1545).hide().show('');
}

function getListMonthLabels(listDate) {
    return listDate.map(date => {
        const localeMonth = new Date(date * 1000).toLocaleString(
            $('html').attr('lang'), {month: 'short'});
        return localeMonth.charAt(0).toUpperCase() + localeMonth.slice(1);
    });
}

function drawPaidArrearsInvoicesStatistic(statistic) {
    const labels = getListMonthLabels(statistic.map((elem) => elem.month));

    const data = {
        labels: labels,
        datasets: [
            {
                label: labelArrears,
                data: statistic.map((elem) => elem.arrears),
                backgroundColor: [config.colors.danger],
                borderColor: [config.colors_label.danger],
                borderWidth: 1
            },
            {
                label: labelPaidArrears,
                data: statistic.map((elem) => elem.paidArrears),
                backgroundColor: [config.colors.success],
                borderColor: [config.colors_label.secondary],
                borderWidth: 1
            }
        ]
    };

    const chartConfig = {
        type: 'bar',
        data: data,
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                    type: 'logarithmic'
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                }
            },
            responsive: true,
            maintainAspectRatio: false
        }
    };

    const barChart = document.querySelector('#arrears-paid-arrears-invoice-statistic');
    if (barChart) {
        const barChartVar = new Chart(barChart, chartConfig);
    }
}

function drawIncomeExpenseStatistic(statistic) {

    const labels = getListMonthLabels(statistic.map((elem) => elem.month));
    const data = {
        labels: labels,
        datasets: [
            {
                label: labelIncome,
                data: statistic.map((elem) => elem.allIncomes),
                backgroundColor: [config.colors.success],
                borderColor: [config.colors_label.success],
                borderWidth: 1
            },
            {
                label: labelExpenses,
                data: statistic.map((elem) => elem.allExpenses),
                backgroundColor: [config.colors.danger],
                borderColor: [config.colors_label.danger],
                borderWidth: 1
            }
        ]
    };

    const chartConfig = {
        type: 'bar',
        data: data,
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                legend: {
                    position: 'bottom'
                }
            },
            responsive: true,
            maintainAspectRatio: false
        }
    };

    const barChart = document.querySelector('#income-expense-chart');
    if (barChart) {
        const barChartVar = new Chart(barChart, chartConfig);
    }
}