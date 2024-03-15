let tableLength = 5;
let timer;
let url = window.location.pathname;
let id = url.substring(url.lastIndexOf('/') + 1);
let request = {
    page: 0,
    pageSize: tableLength,
    number: "",
    date: "",
    status: "",
    apartmentId: ""
};

$(document).ready(function () {
    getInvoices(0);
    initializeStatusSelect();
    initializeFlatPickr();
});
function getInvoices(currentPage) {
    blockCardDody();
    let link;
    if(id.localeCompare("invoices") === 0){
        link = "invoices/get";
        request.apartmentId = "";
    } else {
        link = "get";
        request.apartmentId = id;
    }
    request.page = currentPage;
    request.pageSize = tableLength;
    $.ajax({
        type: "GET",
        url: link,
        data: request,
        success: function (response) {
            $("tbody").children().remove();
            $(".card-footer").children().remove();
            drawTable(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}
function drawTable(response) {
    if (response.numberOfElements == 0) {
        let tdCount = $("td").length;
        $("tbody").append(`<tr class="tr"><td colspan="${tdCount}" class="text-center">${dataNotFound}</td>></tr>`);
    } else {
        for (const invoice of response.content) {
            $("tbody")
                .append(
                    `<tr class="tr text-nowrap" data-href="view-invoice/${invoice.id}">
                    <td>${invoice.number}</td>
                    <td>${invoice.creationDate}</td>
                    <td>${getStatusSpan(invoice.status)}</td>
                    <td>${invoice.paid}</td>
                    <td>${invoice.totalPrice}</td>
                    </tr>`);
        }
        if (response.totalPages > 0) {
            const page = response.pageable.pageNumber;
            drawPaginationElements(response, "getInvoices");
            drawPagination(response.totalPages, page, 'getInvoices');
        }
        addListenerToRow();
    }
}
function addListenerToRow() {
    $('tr[data-href]').find('td').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}
function getStatusSpan(status) {
    switch (status) {
        case 'PAID':
            return '<span class="badge bg-label-success">'+ paidStatus +'</span>';
        case 'UNPAID':
            return '<span class="badge bg-label-danger">'+ unpaidStatus +'</span>';
        case 'PARTLY_PAID':
            return '<span class="badge bg-label-warning">'+ partlyPaidStatus +'</span>';
    }
}
function initializeStatusSelect() {
    $('#filter-by-status').select2({
        dropdownParent: $('#dropdownParent'),
        minimumResultsForSearch: -1,
        placeholder:"",
        allowClear: true,
        ajax: {
            type: "GET",
            url: "invoices/get-statuses",
            processResults: function (response) {
                return {
                    results: $.map(response, function (item) {
                        return {
                            text: getStatus(item),
                            id: item
                        }
                    })
                };
            }

        }
    });
}
function getStatus(status) {
    switch (status) {
        case 'PAID':
            return paidStatus;
        case 'UNPAID':
            return unpaidStatus;
        case 'PARTLY_PAID':
            return partlyPaidStatus;
    }
}
function initializeFlatPickr() {
    $("#filter-by-date").flatpickr({
        dateFormat: "d.m.Y"
    });
}

$("#filter-by-number").on("input", function () {
    request.number = $(this).val();
    searchAfterDelay();
});
$("#filter-by-status").on("change", function () {
    request.status = $(this).val();
    searchAfterDelay();
});
$("#filter-by-date").on("change", function () {
    request.creationDate = $(this).val();
    searchAfterDelay();
});

function searchAfterDelay() {
    clearTimeout(timer);
    timer = setTimeout(function() {
        getInvoices(0);
    }, 800);
}

$('.clear-filters').on('click', function () {
    $('#filter-by-date, #filter-by-status')
        .val('').trigger('change');
    $('#filter-by-number')
        .val('').trigger('input');
})