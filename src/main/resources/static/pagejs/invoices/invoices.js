let timer;
let tableLength = 5;
let request = {
    page: 0,
    pageSize: tableLength,
    number: '',
    status: '',
    apartmentNumber: '',
    ownerId: '',
    processed: '',
    creationDate: '',
    monthDate: ''
};

$(document).ready(function () {
    getInvoices(0);
    initializeSelects();
    initializeFlatPickr();
});
function getInvoices(currentPage) {
    blockCardDody();
    request.page = currentPage;
    request.pageSize = tableLength;
    $.ajax({
        type: "GET",
        url: "invoices/get",
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
            var parts = invoice.creationDate.split('.');
            let date = new Date(parts[2],parts[1]-1,parts[0]).toLocaleString(dateLocale,{month:'long', year:'numeric'});
            $("tbody")
                .append(
                    `<tr class="tr text-nowrap" data-href="invoices/view-invoice/${invoice.id}">
                    <td><input class="form-check-input" type="checkbox"></td>
                    <td>${invoice.number}</td>
                    <td>${getStatusSpan(invoice.status)}</td>
                    <td>${invoice.creationDate}</td>
                    <td>${date}</td>
                    <td>${invoice.apartment}</td>
                    <td>${invoice.ownerFullName}</td>
                    <td>${getProcessed(invoice.isProcessed)}</td>
                    <td>${invoice.paid}</td>
                    <td>${invoice.totalPrice}</td>
                    <td>
                    <div class="dropdown">
                        <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                            <i class="ti ti-dots-vertical"></i>
                        </button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="../edit/${invoice.id}">
                                <i class="ti ti-file me-1"></i>
                            </a>
                            <a class="dropdown-item" href="invoices/edit/${invoice.id}">
                                <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                            </a>
                            <button type="button" class="dropdown-item btn justify-content-start" onclick="openDeleteModal(${invoice.id})">
                                <i class="ti ti-trash me-1"></i>${buttonLabelDelete}
                            </button>
                    </td> </tr>`);
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
    $('tr[data-href] td:not(:last-child)').on('click', function () {
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
function getProcessed(isProcessed) {
    return isProcessed? processed: notProcessed;
}
function initializeSelects() {
    initializeStatusSelect();
    initializeOwnerSelect();
    $("#filter-by-processed").select2({
        language: "uk",
        dropdownParent: $("#dropdownParent"),
        minimumResultsForSearch: -1,
        placeholder: " ",
        allowClear: true
    });
}

function initializeStatusSelect() {
    $("#filter-by-status").select2({
        language: "uk",
        dropdownParent: $("#dropdownParent"),
        minimumResultsForSearch: -1,
        placeholder: " ",
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
function initializeOwnerSelect() {
    $('#filter-by-owner').select2({
        dropdownParent: $('#dropdownParent'),
        language: "uk",
        placeholder:"",
        allowClear: true,
        maximumInputLength: 100,
        ajax: {
            type: "get",
            url: "invoices/get-owners",
            data: function (params) {
                return {
                    search: params.term,
                    page: params.page || 1
                };
            },
            processResults: function (response) {
                return {
                    results: $.map(response.content, function (item) {
                        return {
                            text: item.name,
                            id: item.id
                        }
                    }),
                    pagination: {
                        more: (response.pageable.pageNumber + 1) < response.totalPages
                    }
                };
            }

        }
    });
}
function initializeFlatPickr() {
    $("#filter-by-date").flatpickr({
        locale: "uk",
        dateFormat: "d.m.Y"
    });
    $("#filter-by-month").flatpickr({
        locale: "uk",
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
$("#filter-by-month").on("change", function () {
    request.monthDate = $(this).val();
    searchAfterDelay();
});
$("#filter-by-apartment").on("input", function () {
    $(this).val($(this).val().replace(/[^0-9]/g, ''));
    request.apartmentNumber = $(this).val();
    searchAfterDelay();
});
$("#filter-by-owner").on("change", function () {
    request.ownerId = $(this).val();
    searchAfterDelay();
});
$("#filter-by-processed").on("change", function () {
    request.processed = $(this).val();
    searchAfterDelay();
});
function searchAfterDelay() {
    clearTimeout(timer);
    timer = setTimeout(function() {
        getInvoices(0);
    }, 800);
}

$('.clear-filters').on('click', function () {
    $('#filter-by-month, #filter-by-date, #filter-by-owner, #filter-by-status, #filter-by-processed')
        .val('').trigger('change');
    $('#filter-by-number, #filter-by-apartment')
        .val('').trigger('input');
})
