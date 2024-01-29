$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "roles/getRoles",
        data: {},
        success: function (response) {
            setPermissions(response);
        },
        error: function () {
            toastr.error(errorMessage)
        }
    });
});

function setPermissions (response){
    let i = 0;
    $("input[name='managerChecks']").each(function() {
        $(this).prop('checked', response.managerAllowances[i]);
        i++;
    });
    i=0;
    $("input[name='accountantChecks']").each(function() {
        $(this).prop('checked', response.accountantAllowances[i]);
        i++;
    });
    i=0;
    $("input[name='electricianChecks']").each(function() {
        $(this).prop('checked', response.electricianAllowances[i]);
        i++;
    });
    i=0;
    $("input[name='plumberChecks']").each(function() {
        $(this).prop('checked', response.plumberAllowances[i]);
        i++;
    });
}

$("#saveButton").on("click", function(){
    $(this).prop('disabled', true);
    const managerChecks = [];
    const electricianChecks = [];
    const plumberChecks = [];
    const accountantChecks = [];
    $("input[name='managerChecks']").each(function() {
        managerChecks.push(this.checked);
    });
    $("input[name='accountantChecks']").each(function() {
        accountantChecks.push(this.checked);
    });
    $("input[name='electricianChecks']").each(function() {
        electricianChecks.push(this.checked);
    });
    $("input[name='plumberChecks']").each(function() {
        plumberChecks.push(this.checked);
    });
    $.ajax({
        type: "POST",
        url: "roles/update",
        data: {
            managerPermissions: managerChecks,
            accountantPermissions: accountantChecks,
            electricianPermissions: electricianChecks,
            plumberPermissions: plumberChecks
        },
        success: async function () {
            toastr.success(successMessage);
            setTimeout(() => location.reload(), 6000)
        },
        error: function () {
            toastr.error(errorMessage)
        }
    });
});
