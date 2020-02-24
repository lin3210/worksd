// JavaScript Document
    /*
	  分页数据源
	*/
    function page_data_g()
	{
		  var curPage = parseInt($("#cupage_g").val());
		  var total = parseInt($("#total_g").val());
		  var html_lis="<ul class='pagination'>";
		  var ik=5;
		  if(total<=5)
		  {
			  if(total==1)
			  {
				  html_lis += "<li class='prev disabled' id='befora'> <a href='javascript:void(0);'>Trang đầu</a><a href='javascript:void(0)'>Trang trước</a></li>";
				  html_lis +="<li class='active'><a href='javascript:void(0)'>"+curPage+"</a></li>";
				  html_lis +="<li class='next disabled' id='b'><a href='javascript:void(0)'>Trang sau</a><a href='javascript:void(0)'>Trang cuối </a></li></ul>";
			  }
			  else if(total<1)
			  {
				  $("#currentpage").html(0);
				  html_lis += "<li class='prev disabled' id='befora'> <a href='javascript:void(0);'>Trang đầu</a><a href='javascript:void(0)'>Trang trước</a></li>";
				  html_lis +="<li class='next disabled' id='b'><a href='javascript:void(0)'>Trang sau</a><a href='javascript:void(0)'>Trang cuối </a></li></ul>";
			  }
			  else
			  {
				 ik=total;
				 html_lis +=page_list_g(ik,curPage,total);
			  }
		  }
		  else
		  {
			   html_lis +=page_list_g(ik,curPage,total);
		  }
		  $("#datatable_g").html(html_lis);
   }
   
   /*
    分页列表
   */
   function page_list_g(ik,curPage,total)
   {
	   var html_li="";
	   if(curPage==1)
		{
			html_li += "<li class='prev disabled' id='befora'> <a href='javascript:void(0);'>Trang đầu</a><a href='javascript:void(0)'>Trang trước</a></li>";
			for(var k=1;k<=ik;k++)
			{
				if(k==1)
				{
					html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_g').val("+curPage+");load_data_g();>"+curPage+"</a></li>";
				}
				else
				{
					html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage+k-1)+");load_data_g();>"+(curPage+k-1)+"</a></li>";
				}
			}
			html_li +=" <li class='next' id='b'><a href='javascript:void(0)' onclick=$('#cupage_g').val(parseInt($('#cupage_g').val())+1);load_data_g();>Trang sau</a><a href='javascript:void(0)' onclick=$('#cupage_g').val("+total+");load_data_g();>Trang cuối </a></li></ul>";
		}
		if(curPage>1&&curPage<total)
		{
			html_li += "<li class='prev' id='befora'> <a href='javascript:void(0)'  onclick=$('#cupage_g').val(1);load_data_g();>Trang đầu</a><a href='javascript:void(0)'  onclick=$('#cupage_g').val(parseInt($('#cupage_g').val())-1);load_data_g();>Trang trước</a></li>";
			if(curPage==2)
			{
				for(var k=1;k<=ik;k++)
				{
					if(k==1)
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage-1)+");load_data_g();>"+(curPage-1)+"</a></li>";
					}
					else if(k==2)
					{
						html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_g').val("+curPage+");load_data_g();>"+curPage+"</a></li>";
					}
					else
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage+k-2)+");load_data_g();>"+(curPage+k-2)+"</a></li>";
					} 
				} 
			}
			else if(curPage==(total-1))
			{
				for(var k=1;k<=ik;k++)
				{
					if(k==1)
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage-(ik-2))+");load_data_g();>"+(curPage-(ik-2))+"</a></li>";
					} 
					else if(k==(ik-1))
					{
						html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_g').val("+curPage+");load_data_g();>"+curPage+"</a></li>";
					}
					else
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage+k-(ik-1))+");load_data_g();>"+(curPage+k-(ik-1))+"</a></li>";
					} 
				}
			}
			else
			{
				for(var k=1;k<=ik;k++)
				{
					if(k<3)
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage-(ik-k-2))+");load_data_g();>"+(curPage-(ik-k-2))+"</a></li>";
					}
					else if(k==3)
					{
						html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_g').val("+curPage+");load_data_g();>"+curPage+"</a></li>";
					}
					else
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage+(k-3))+");load_data_g();>"+(curPage+(k-3))+"</a></li>";
					} 
				}
			}
			html_li+="<li class='next' id='b'><a   href='javascript:void(0)' onclick=$('#cupage_g').val(parseInt($('#cupage_g').val())+1);load_data_g();>Trang sau</a><a href='javascript:void(0)' onclick =$('#cupage_g').val("+total+");load_data_g();>Trang cuối </a></li></ul>";
		  }
		  if(curPage==total)
		  {
			  html_li += "<li class='prev' id='befora'> <a href='javascript:void(0)' onclick =$('#cupage_g').val(1);load_data_g();>Trang đầu</a><a href='javascript:void(0)' onclick =$('#cupage_g').val(parseInt($('#cupage_g').val())-1);load_data_g();>Trang trước</a></li>";   
			  for(var k=1;k<=ik;k++)
			  {
				if(k==ik)
				{
					html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_g').val("+curPage+");load_data_g();>"+curPage+"</a></li>";
				}
				else
				{
					html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_g').val("+(curPage-(ik-k))+");load_data_g();>"+(curPage-(ik-k))+"</a></li>";
				}
			  }
			  html_li+="<li class='next disabled' id='b'><a href='javascript:void(0)'>Trang sau</a><a href='javascript:void(0)'>Trang cuối </a></li></ul>";
		 }
		 return html_li;
   }
   
   /*
     为分页赋值
   */
   function page_value_g(data)
	{
		$("#totalrows_g").html(data.list.totalRows);
		$("#numperpage_g").html(data.list.numPerPage);
		$("#currentpage_g").html(data.list.currentPage);
		$("#totalpages_g").html(data.list.totalPages);
		$("#cupage_g").val(data.list.currentPage);
		$("#total_g").val(data.list.totalPages);
	}
	
    /*
	文本框为空时为时间值赋空值
	*/
   function onb()
   {
	  
	   var dateTime= $("#date1").val();
	   if(dateTime==null || dateTime=="")
	   {
		   $("#starTime").val("");
		   $("#endTime").val("");
	   }
   }
   
   
   