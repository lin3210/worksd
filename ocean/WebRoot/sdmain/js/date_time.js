// JavaScript Document
      /*
     	时间按钮
	   */
	   var dateRange1 = new pickerDateRange('date1', {
			isTodayValid : true,
			needCompare : false,
			defaultText : ' Để',
			autoSubmit : true,
			stopToday:true,
			theme : 'ta',
			success : function(obj) {
			$("#starTime").val(obj.startDate);
			$("#endTime").val(obj.endDate);
		  }
		});
	 