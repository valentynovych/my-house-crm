let tableLength = 2;
let timer;
let request = {
    page: 0,
    pageSize: tableLength,
    id: "",
    fullName: "",
    phoneNumber: "",
    email: "",
    houseId: "",
    apartment: "",
    creationDate: "",
    status: "",
    debt: true
};


$(document).ready(function () {
    getOwners(0);
    initializeStatusSelect();
    initializeDebtSelect();
    initializeFlatPickr();
});
function initializeStatusSelect() {
    $('#filter-by-status').select2({
        language: "uk",
        dropdownParent: $('#dropdownParent'),
        minimumResultsForSearch: -1,
        allowClear: true,
        ajax: {
            type: "GET",
            url: "owners/get-statuses",
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
function initializeDebtSelect() {
    $('#filter-by-debt').select2({
        language: "uk",
        minimumResultsForSearch: -1,
        dropdownParent: $('#dropdownParent'),
        placeholder: " ",
        allowClear: true
    });
}
function getStatus(status) {
    switch (status) {
        case 'NEW':
            return newStatus;
        case 'ACTIVE':
            return activeStatus;
        case 'DISABLED':
            return disabledStatus;
    }
}
function initializeFlatPickr() {
    $("#filter-by-creation-date").flatpickr({
        locale: "uk",
        dateFormat: "d.m.Y"
    });
}

$("#filter-by-id").on("input", function () {
    request.id = $(this).val();
    searchAfterDelay();
});
$("#filter-by-fullName").on("input", function () {
    request.fullName = $(this).val();
    searchAfterDelay();
});
$("#filter-by-phoneNumber").on("input", function () {
    request.phoneNumber = $(this).val();
    searchAfterDelay();
});
$("#filter-by-email").on("input", function () {
    request.email = $(this).val();
    searchAfterDelay();
});
$("#filter-by-house").on("change", function () {
    request.houseId = $(this).val();
    searchAfterDelay();
});
$("#filter-by-apartment").on("input", function () {
    request.apartment = $(this).val();
    searchAfterDelay();
});
$("#filter-by-creation-date").on("change", function () {
    request.creationDate = $(this).val();
    searchAfterDelay();
});
$("#filter-by-debt").on("change", function () {
    request.debt = $(this).val();
    searchAfterDelay();
});
$("#filter-by-status").on("change", function () {
    request.status = $(this).val();
    searchAfterDelay();
});

function searchAfterDelay() {
    clearTimeout(timer);
    timer = setTimeout(function() {
        getOwners(0);
    }, 800);
}

function getOwners(currentPage) {
    blockCardDody();
    request.page = currentPage;
    request.pageSize = tableLength;
    $.ajax({
        type: "GET",
        url: "owners/getOwners",
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
        for (const owner of response.content) {
            $("tbody")
                .append(
                    `<tr class="tr text-nowrap">
                    <td>${owner.id}</td>
                    <td>${owner.fullName}</td>
                    <td>${owner.phoneNumber}</td>
                    <td>${owner.email}</td>
                    <td>${owner.house}</td>
                    <td>${owner.apartment}</td>
                    <td>${owner.creationDate}</td>
                    <td>${getStatusSpan(owner.status)}</td>
                    <td>${owner.hasDebt}</td>
                    <td>
                    <div class="dropdown">
                        <button type="button" class="btn p-0 dropdown-toggle hide-arrow" data-bs-toggle="dropdown">
                            <i class="ti ti-dots-vertical"></i>
                        </button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="owners/edit/${owner.id}">
                                <i class="ti ti-pencil me-1"></i>${buttonLabelEdit}
                            </a>
                            <button type="button" class="dropdown-item btn justify-content-start" onclick="openDeleteModal(${owner.id})">
                                <i class="ti ti-trash me-1"></i>${buttonLabelDelete}
                            </button>
                    </td> </tr>`);
        }
        if (response.totalPages > 0) {
            const page = response.pageable.pageNumber;
            drawPaginationElements(response, "getOwners");
            drawPagination(response.totalPages, page, 'getOwners');
        }
    }
}
function getStatusSpan(status){
    switch (status) {
        case 'NEW':
            return '<span class="badge bg-label-info">'+ newStatus +'</span>';
        case 'ACTIVE':
            return '<span class="badge bg-label-success">'+ activeStatus +'</span>';
        case 'DISABLED':
            return '<span class="badge bg-label-danger">'+ disabledStatus +'</span>';
    }
}

$('.clear-filters').on('click', function () {
    $('#filter-by-house, #filter-by-creation-date, #filter-by-debt, #filter-by-status')
        .val('').trigger('change');
    $('#filter-by-id, #filter-by-fullName, #filter-by-phoneNumber, #filter-by-email, #filter-by-apartment')
        .val('').trigger('input');
})
function openDeleteModal(id) {

}