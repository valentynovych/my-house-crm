let url = window.location.pathname;
let id = url.substring(url.lastIndexOf('/') + 1);
let totalPrice;
$(document).ready(function () {
    $("#download-link").attr("href", "download-in-pdf/" + id);
    getInvoice();
});
function getInvoice() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "get/"+id,
        success: function (response) {
            $("tbody").children().remove();
            let invoiceNumber = invoice+response.number;
            $(".invoice-breadcrumb").text(invoiceNumber);
            $(".breadcrumb").append(
                `<li class="breadcrumb-item active">
                    <span>${invoiceNumber}</span>
                </li>`);
            drawTable(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function drawTable(response) {
    let i = 1;
    for (const invoice of response.invoiceItemResponses) {
        $("tbody")
            .append(
                `<tr class="tr text-nowrap">
                    <td>${i}</td>
                    <td>${invoice.serviceName}</td>
                    <td>${invoice.amount}</td>
                    <td>${invoice.unitName}</td>
                    <td>${invoice.pricePerUnit}</td>
                    <td>${invoice.cost}</td>
                    </tr>`);
        i++;
    }
    $("tbody")
        .append(
            `<tr class="tr text-nowrap">
            <td colspan="5"></td>
            <td>${total+": "+response.totalPrice}</td>
            </tr>`);
}

$("#print-button").on("click", function () {
    let tableJson = tableToJson();
    printJS({
        printable: tableJson,
        properties: ['#', 'Послуга', 'Кількість споживання',
        'Одиниця виміру', 'Ціна за одиницю, грн', 'Вартість, грн'],
        type: 'json',
        header: '<h3>'+$(".invoice-breadcrumb").text()+'</h3>',
    });
});

function tableToJson() {
    let myRows = [];
    let $headers = $("th");
    $("tbody tr").each(function(index) {
        $cells = $(this).find("td");
        myRows[index] = {};
        $cells.each(function(cellIndex) {
            myRows[index][$($headers[cellIndex]).html()] = $(this).html();
        });
    });
    let lastRow = myRows[myRows.length - 1];
    $headers.each(function () {
        if($(this).text().localeCompare("Послуга") === 0){
            totalPrice = lastRow[$(this).text()];
        }
        if( $(this).text().localeCompare("Вартість, грн") === 0) {
            lastRow[$(this).text()] = totalPrice;
        } else {
            lastRow[$(this).text()] = '';
        }
    });
    return myRows;
}