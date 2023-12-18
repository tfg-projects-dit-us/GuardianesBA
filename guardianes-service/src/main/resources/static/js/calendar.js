// Obtener la fecha actual
var currentDate = new Date();

// Obtener el primer día del próximo mes
var nextMonthFirstDay = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1);

// Obtener el último día del próximo mes
var nextMonthLastDay = new Date(currentDate.getFullYear(), currentDate.getMonth() + 2, 0);

// Initialization of components
$('#datepicker').datepicker({
	format: 'dd/mm/yy',
	language: 'es',
	maxViewMode: 0, // Only show the days of the month
	autoclose: true, // Close when a date is selected
	multidate: true,
	weekStart: 1, // Monday
    beforeShowDay: function(date){
        var day = date.getDay();
        return day !== 0 && day !== 6;
    }
}).datepicker('setStartDate', nextMonthFirstDay).datepicker('setEndDate', nextMonthLastDay); // Establecer fechas de inicio y fin


// Preseleccionar sábados y domingos
var selectedDates = [];
for (var i = 1; i <= 31; i++) {
    var tempDate = new Date(nextMonthFirstDay.getFullYear(), nextMonthFirstDay.getMonth(), i);
    if (tempDate.getDay() === 0 || tempDate.getDay() === 6) {
        selectedDates.push(tempDate);
    }
}


$('#datepicker').datepicker('setDates', selectedDates);

