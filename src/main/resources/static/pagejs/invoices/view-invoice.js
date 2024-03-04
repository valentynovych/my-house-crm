let url = window.location.pathname;
let id = url.substring(url.lastIndexOf('/') + 1);
$(document).ready(function () {
    $("#edit-invoice-link").attr("href", "../edit/" + id);
    getInvoice();
});

function getInvoice() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "get/" + id,
        success: function (response) {
            console.log(response);
            setFields(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function setFields(response) {
    const responseMap = new Map(Object.entries((response)));
    responseMap.forEach((value, key) => {
        $("#" + key).text(value);
    });
    $(".invoice-breadcrumb").each(function () {
        $(this).text(breadCrumb + response.number);
    });
    let number = "";
    for (let j = 0; j < 10 - response.accountNumber.toString().length; j++) {
        number += "0";
    }
    number += response.accountNumber;
    let accountNumber = number.substring(0, 5) + "-" + number.substring(5, 10)
    $("#account").text(accountNumber);
    var parts = response.creationDate.split('.');
    let month = new Date(parts[2], parts[1] - 1, parts[0]).toLocaleString(dateLocale, {month: 'long', year: 'numeric'});
    $("#month").text(month);
    $("#status").append(getStatusSpan(response.invoiceStatus));
    $("#isProcessed").append(getProcessedSpan(response.isProcessed));
    $("#number").val(response.number);
    $("#creationDate").val(response.creationDate);
    setServiceTable(response.itemResponses, response.totalPrice);
}

function getStatusSpan(status) {
    switch (status) {
        case 'PAID':
            return '<span class="badge bg-label-success">' + paidStatus + '</span>';
        case 'UNPAID':
            return '<span class="badge bg-label-danger">' + unpaidStatus + '</span>';
        case 'PARTLY_PAID':
            return '<span class="badge bg-label-warning">' + partlyPaidStatus + '</span>';
    }
}

function getProcessedSpan(isProcessed) {
    return isProcessed ? '<span class="badge bg-label-success">' + processed + '</span>' :
        '<span class="badge bg-label-danger">' + notProcessed + '</span>';
}

function setServiceTable(itemResponses, totalPrice) {
    let i = 1;
    for (let item of itemResponses) {
        $("#service-table").append(
            `<tr>
            <td class="px-2">
                ${i}
            </td>
            <td class="px-2">
                ${item.serviceName}
            </td>
            <td class="px-2">
                ${item.amount}
            </td>
            <td class="px-2">
                ${item.unitName}
            </td>
            <td class="px-2">
                ${item.pricePerUnit}
            </td>
            <td class="px-2">
                ${item.cost}
            </td>
        </tr>`
        );
        i++;
    }
    $("#service-table").append(
        `<tr>
            <td colspan="5"></td>
            <td colspan="1" class="text-nowrap"><span>${total + ": "}</span><span>${totalPrice}</span></td>
        </tr>`
    );
}