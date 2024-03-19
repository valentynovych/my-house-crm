const apartmentId = window.location.pathname.match(/\d+/);

$(document).ready(function () {
    blockBy('.count-statistic')
    $.ajax({
        url: '../statistic/get-general-statistic?apartment=' + apartmentId,
        type: 'get',
        success: function (response) {
            fillGeneralStatistic(response);
        },
        error: function (error) {
            console.log(error);
        }
    }).done(function () {
        unblockBy('.count-statistic')
    })

    blockBy('.expense-per-month-chart')
    $.ajax({
        url: '../statistic/get-expense-per-month?apartment=' + apartmentId,
        type: 'get',
        success: function (response) {
            drawExpensePerMonthChart(response);
        },
        error: function (error) {
            console.log(error);
        }
    }).done(function () {
        unblockBy('.expense-per-month-chart');
    });

    blockBy('.expense-per-year-chart')
    $.ajax({
        url: '../statistic/get-expense-per-year?apartment=' + apartmentId,
        type: 'get',
        success: function (response) {
            drawExpensePerYearChart(response);
        },
        error: function (error) {
            console.log(error);
        }
    }).done(function () {
        unblockBy('.expense-per-year-chart');
    })

    blockBy('.expense-per-year-on-month-chart');
    $.ajax({
        url: '../statistic/get-expense-per-year-on-month?apartment=' + apartmentId,
        type: 'get',
        success: function (response) {
            drawExpensePerYearOnMonthChart(response);
        },
        error: function (error) {
            console.log(error);
        }
    }).done(function () {
        unblockBy('.expense-per-year-on-month-chart');
    })

});


function fillGeneralStatistic(response) {
    const $current = $('#current-balance');
    const currentBalance = response.currentBalance;
    $current.text(`${currentBalance} ${currency}`);
    if (currentBalance < 0) {
        $current.closest('.card').addClass('bg-label-danger');
    }
    $('#personal-account').text(`№ ${response.personalAccountNumber}`);
    $('#average-on-last-month').text(`${response.expenseOnLastMonth} ${currency}`);
}

function drawExpensePerMonthChart(response) {

    const expensePerMonthChart = document.getElementById('expense-per-month-chart');

    if (response.length > 0) {
        if (expensePerMonthChart) {
            const doughnutChartVar = new Chart(expensePerMonthChart, {
                type: 'pie',
                data: {
                    labels: response.map((elem) => elem.itemName),
                    datasets: [
                        {
                            data: response.map((elem) => +elem.itemValue),
                            backgroundColor: [config.colors_label.primary, config.colors_label.info, config.colors_label.warning],
                            pointStyle: 'rectRounded',
                            hoverOffset: 4
                        }
                    ]
                },
                options: {
                    animation: {
                        duration: 500
                    },
                    plugins: {
                        colors: {
                            forceOverride: true
                        },
                        legend: {
                            display: true,
                            position: 'bottom'
                        },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    const label = context.label || '',
                                        value = context.parsed;
                                    return ' ' + label + ' : ' + value + `${currency}`;
                                }
                            },
                        }
                    },
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
    } else {
        const parent = expensePerMonthChart.parentElement;
        expensePerMonthChart.remove();
        parent.innerHTML =
            `<div class="d-flex justify-content-center w-75 text-center" style="position: absolute">
                    <h5 class="fw-bold">${paymentsNotFound}</h5>
                </div>`;
        parent.classList += ' d-flex justify-content-center align-items-center';
    }
}

function drawExpensePerYearChart(response) {
    console.log(response);

    const expensePerYearChart = document.getElementById('expense-per-year-chart');
    if (response.length > 0) {
        if (expensePerYearChart) {
            const doughnutChartVar = new Chart(expensePerYearChart, {
                type: 'pie',
                data: {
                    labels: response.map((elem) => elem.itemName),
                    datasets: [
                        {
                            data: response.map((elem) => +elem.itemValue),
                            backgroundColor: [config.colors_label.primary, config.colors_label.info, config.colors_label.warning],
                            pointStyle: 'rectRounded',
                            hoverOffset: 4
                        }
                    ]
                },
                options: {
                    animation: {
                        duration: 500
                    },
                    plugins: {
                        colors: {
                            forceOverride: true
                        },
                        legend: {
                            display: true,
                            position: 'bottom'
                        },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    const label = context.label || '',
                                        value = context.parsed;
                                    return ' ' + label + ' : ' + value + `${currency}`;
                                }
                            },
                        }
                    },
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
    } else {
        const parent = expensePerYearChart.parentElement;
        expensePerYearChart.remove();
        parent.innerHTML =
            `<div class="d-flex justify-content-center w-75 text-center" style="position: absolute">
                    <h5 class="fw-bold">${paymentsNotFound}</h5>
                </div>`;
        parent.classList += ' d-flex justify-content-center align-items-center';
    }
}

function drawExpensePerYearOnMonthChart(response) {
    const barChart = document.querySelector('#expense-per-year-on-month-chart');
    if (response.length > 0) {
        const data = {
            labels: getListMonthLabels(response.map((elem) => elem.month)),
            datasets: [
                {
                    data: response.map((elem) => elem.amount),
                    backgroundColor: [config.colors.primary],
                    borderColor: [config.colors_label.dark],
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
                        display: false
                    }
                },
                responsive: true,
                maintainAspectRatio: false
            }
        };


        if (barChart) {
            const barChartVar = new Chart(barChart, chartConfig);
        }
    } else {
        const parent = barChart.parentElement;
        barChart.remove();
        parent.innerHTML =
            `<div class="d-flex justify-content-center w-75 text-center" style="position: absolute">
                    <h5 class="fw-bold">${paymentsNotFound}</h5>
                </div>`;
        parent.classList += ' d-flex justify-content-center align-items-center';
    }
}

function getListMonthLabels(listDate) {
    return listDate.map(date => {
        const localeMonth = new Date(date * 1000).toLocaleString(
            $('html').attr('lang'), {month: 'short', year: '2-digit'});
        return localeMonth.charAt(0).toUpperCase() + localeMonth.slice(1);
    });
}

//
// <div className="col-sm-12 col-md-6">
//     <div className="card" style="height: 300px">
//         <div className="card-body">
//             <canvas id="expense-per-month-chart"></canvas>
//         </div>
//     </div>
// </div>
// <div className="col-sm-12 col-md-6">
//     <div className="card" style="height: 300px">
//         <div className="card-body">
//             <canvas id="expense-per-year-chart"></canvas>
//         </div>
//     </div>
// </div>
// <canvas id="expense-per-year-on-month-chart"></canvas>