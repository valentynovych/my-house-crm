let tableLength = 5;
let currentPage = 0;
let byName = '';
let byRole = '';
let byPhone = '';
let byEmail = '';
let byStatus = '';
let timer;

let $role = $('#filter-by-role');
$role.select2({
    dropdownParent: $('#filterRoleWrapper'),
    minimumResultsForSearch: -1,
    ajax: {
        type: "GET",
        url: 'staff/get-roles',
        processResults: function (response) {
            return {
                results: $.map(response, function (role) {
                    return {
                        id: role.id,
                        text: getRoleLabel(role.name)
                    }
                })
            };
        }
    }
});

let $status = $('#filter-by-status');
$status.select2({
    dropdownParent: $('#filterStatusWrapper'),
    minimumResultsForSearch: -1,
    ajax: {
        type: "GET",
        url: 'staff/get-statuses',
        dataType: 'json',
        processResults: function (response) {
            return {
                results: $.map(response, function (status) {
                    return {
                        id: status,
                        text: getStatusLabel(status)
                    }
                })
            };
        }
    }
});
$(window).on("load", function () {
    getStaff(currentPage);
})

$('#filter-by-name').on('input', function () {
    byName = this.value;
    delayBeforeSearch();
})
$role.on('change', function () {
    byRole = this.value;
    delayBeforeSearch();
})
$status.on('change', function () {
    byStatus = this.value;
    delayBeforeSearch();
})
$('#filter-by-phone').on('input', function () {
    byPhone = this.value;
    delayBeforeSearch();
})
$('#filter-by-email').on('input', function () {
    byEmail = this.value;
    delayBeforeSearch();
})

function delayBeforeSearch() {
    console.log("start")
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getStaff(0)
    }, keyPause);
}


function getStaff(page) {

    let url = new URL('system-settings/staff/get-staff', window.location.origin);
    url.searchParams.append('page', page);
    url.searchParams.append('pageSize', tableLength);
    if (byName) {
        url.searchParams.append('name', byName);
    }
    if (byRole) {
        url.searchParams.append('role', byRole);
    }
    if (byPhone) {
        url.searchParams.append('phone', byPhone);
    }
    if (byEmail) {
        url.searchParams.append('email', byEmail);
    }
    if (byStatus) {
        url.searchParams.append('status', byStatus);
    }

    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
            console.log(result);
            currentPage = page;
            clearTableLine();
            $(".card-footer").children().remove();
            drawTable(result);
        },
        error: function () {
            toastr.error("Упс.. Виникла помилка");
        }
    });
}

function getRoleLabel(role) {
    switch (role) {
        case 'DIRECTOR':
            return roleDirector;
        case 'MANAGER':
            return roleManager;
        case 'ACCOUNTANT':
            return roleAccountant;
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
    }
}

function getStatusLabel(status) {
    switch (status) {
        case 'NEW':
            return statusNew;
        case 'ACTIVE':
            return statusActive;
        case 'DISABLED':
            return statusDisabled;
        default:
            return 'Не відомий'
    }
}

function drawTable(result) {
    const page = result.pageable.pageNumber;
    for (const staff of result.content) {


        const badgeStatus = staff.status === 'NEW'
            ? '<span class="badge bg-label-success me-1">' + statusNew + '</span>'
            : staff.status === 'ACTIVE'
                ? '<span class="badge bg-label-success me-1">' + statusActive + '</span>'
                : '<span class="badge bg-label-danger me-1">' + statusDisabled + '</span>';

        $('<tr>\n' +
            '<td>' + staff.firstName + ' ' + staff.lastName + '</td>\n' +
            '<td>' + getRoleLabel(staff.role.name) + '</td>\n' +
            '<td>' + staff.phoneNumber + '</td>\n' +
            '<td>' + staff.email + '</td>\n' +
            '<td>' + badgeStatus + '</td>\n' +
            '<td>\n' +
            '  <div class="dropdown">\n' +
            '   <button type="button" class="btn p-0 dropdown-toggle hide-arrow"\n' +
            '           data-bs-toggle="dropdown">\n' +
            '    <i class="ti ti-dots-vertical"></i>\n' +
            '     </button>\n' +
            '     <div class="dropdown-menu">\n' +
            '     <a class="dropdown-item" href="staff/view-staff/' + staff.id + '">\n' +
            '     <i class="ti ti-pencil me-1"></i>Перегляд\n' +
            '     </a>\n' +
            '     <button type="button" class="dropdown-item btn justify-content-start" data-bs-toggle="modal" data-bs-target="#modalCenter"' +
            '     onclick="addDeleteLink(' + staff.id + ')">\n' +
            '     <i class="ti ti-trash me-1"></i>Видалити\n' +
            '     </button>\n' +
            '     </div>\n' +
            '     </div>\n' +
            '</td>\n' +
            '</tr>').appendTo("tbody");
    }

    if (result.totalPages > 0) {
        $('<div class="details-table d-flex gap-2 align-items-center"></div>').appendTo('.card-footer')

        const size = result.size;

        const from = page > 0 ? (page * size) + 1 : 1;
        const to = from + result.numberOfElements - 1;
        const total = result.totalElements;

        if (from === total) {
            $('<div class="dataTables_info"">' +
                'Показано ' + from + ' з ' + total + ' користувачів' +
                '</div>').appendTo(".details-table");
        } else {
            $('<div class="dataTables_info">' +
                'Показано ' + from + '-' + to + ' з ' + total + ' користувачів' +
                '</div>').appendTo(".details-table")
        }

        $('<label class="ms-3">Показати по: </label>' +
            '<div class="selecte-wrapper"><select name="tables_length" class="form-select form-select-sm">\n' +
            '      <option value="2">2</option>\n' +
            '      <option value="5">5</option>\n' +
            '      <option value="10">10</option>\n' +
            '       <option value="20">20</option>\n' +
            '</select> </div>').appendTo(".details-table");

        var $select = $('select[name="tables_length"]');
        $select.on("change", function () {
            tableLength = this.value;
            getStaff(0);
        });
        $select.val(tableLength);

        if (result.totalPages > 1) {
            showPagination(result.totalPages);

            switch (page) {
                case (result.totalPages - 1):
                    $(".page-item.last").addClass('disabled');
                    $(".page-item.next").addClass('disabled');
                    break;
                case 0:
                    $(".page-item.first").addClass('disabled');
                    $(".page-item.prev").addClass('disabled');
                    break;
            }
        }
    }

    function showPagination(countItems) {
        var paginationList = '<ul class="pagination pagination-sm">\n' +
            '<li class="page-item first">\n' +
            '   <a class="page-link waves-effect" onclick="getStaff(0)"><i class="ti ti-chevrons-left tf-icon fs-6"></i></a>\n' +
            '</li>\n' +
            '<li class="page-item prev">\n' +
            '   <a class="page-link waves-effect" onclick="getStaff(' + (page - 1) + ')"><i class="ti ti-chevron-left tf-icon fs-6"></i></a>\n' +
            '</li>\n';
        for (let item = 0; item < countItems; item++) {
            paginationList += '<li class="page-item ' + (page === item ? 'active' : '') + '">\n' +
                '<a class="page-link waves-effect" onclick="getStaff(' + item + ')">' + (item + 1) + '</a>\n' +
                '</li>';
        }
        paginationList += '<li class="page-item next">\n' +
            '       <a class="page-link waves-effect" onclick="getStaff(' + (page + 1) + ')"><i class="ti ti-chevron-right tf-icon fs-6"></i></a>\n' +
            '   </li>\n' +
            '      <li class="page-item last">\n' +
            '       <a class="page-link waves-effect" onclick="getStaff(' + (countItems - 1) + ')"><i class="ti ti-chevrons-right tf-icon fs-6"></i></a>\n' +
            '      </li>\n' +
            '   </ul>\n'

        $(paginationList).appendTo(".card-footer");
    }
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}