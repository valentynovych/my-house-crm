$(window).on("load", function () {

    let staff = {
        id: 0,
        firstName: '',
        lastName: '',
        phoneNumber: '',
        email: '',
        role: {
            id: '',
            name: ''
        }
    };

    staff.id = window.location.pathname.match(/\d+$/);

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

    blockCardDody();
    $.ajax({
        type: 'get',
        url: '../get-staff/' + staff.id,
        success: function (response) {
            staff = response;
            fillTable(staff);
        }, error: function (error) {
            toastr.error(errorMessageStaff);
        }
    })


    function fillTable(staff) {
        $('#current-staff-link').attr('href', '../edit-staff/' + staff.id);

        $('#name').html(staff.firstName + ' ' + staff.lastName);
        $('#phoneNumber').html(staff.phoneNumber);
        $('#email').html(staff.email);
        $('#role').html(getRoleLabel(staff.role.name));
        const badgeStatus = staff.status === 'NEW'
            ? '<span class="badge bg-label-success me-1">' + statusNew + '</span>'
            : staff.status === 'ACTIVE'
                ? '<span class="badge bg-label-success me-1">' + statusActive + '</span>'
                : '<span class="badge bg-label-danger me-1">' + statusDisabled + '</span>';
        $('#status').html(badgeStatus);
    }
})