// JavaScript Document
    /*
	  分页数据源
	*/
    function page_data_a()
	{
		  var curPage = parseInt($("#cupage_a").val());
		  var total = parseInt($("#total_a").val());
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
				 html_lis +=page_list_a(ik,curPage,total);
			  }
		  }
		  else
		  {
			   html_lis +=page_list_a(ik,curPage,total);
		  }
		  $("#datatable_a").html(html_lis);
   }
   
   /*
    分页列表
   */
   function page_list_a(ik,curPage,total)
   {
	   var html_li="";
	   if(curPage==1)
		{
			html_li += "<li class='prev disabled' id='befora'> <a href='javascript:void(0);'>Trang đầu</a><a href='javascript:void(0)'>Trang trước</a></li>";
			for(var k=1;k<=ik;k++)
			{
				if(k==1)
				{
					html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_a').val("+curPage+");load_data_a();>"+curPage+"</a></li>";
				}
				else
				{
					html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage+k-1)+");load_data_a();>"+(curPage+k-1)+"</a></li>";
				}
			}
			html_li +=" <li class='next' id='b'><a href='javascript:void(0)' onclick=$('#cupage_a').val(parseInt($('#cupage_a').val())+1);load_data_a();>Trang sau</a><a href='javascript:void(0)' onclick=$('#cupage_a').val("+total+");load_data_a();>Trang cuối </a></li></ul>";
		}
		if(curPage>1&&curPage<total)
		{
			html_li += "<li class='prev' id='befora'> <a href='javascript:void(0)'  onclick=$('#cupage_a').val(1);load_data_a();>Trang đầu</a><a href='javascript:void(0)'  onclick=$('#cupage_a').val(parseInt($('#cupage_a').val())-1);load_data_a();>Trang trước</a></li>";
			if(curPage==2)
			{
				for(var k=1;k<=ik;k++)
				{
					if(k==1)
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage-1)+");load_data_a();>"+(curPage-1)+"</a></li>";
					}
					else if(k==2)
					{
						html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_a').val("+curPage+");load_data_a();>"+curPage+"</a></li>";
					}
					else
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage+k-2)+");load_data_a();>"+(curPage+k-2)+"</a></li>";
					} 
				} 
			}
			else if(curPage==(total-1))
			{
				for(var k=1;k<=ik;k++)
				{
					if(k==1)
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage-(ik-2))+");load_data_a();>"+(curPage-(ik-2))+"</a></li>";
					} 
					else if(k==(ik-1))
					{
						html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_a').val("+curPage+");load_data_a();>"+curPage+"</a></li>";
					}
					else
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage+k-(ik-1))+");load_data_a();>"+(curPage+k-(ik-1))+"</a></li>";
					} 
				}
			}
			else
			{
				for(var k=1;k<=ik;k++)
				{
					if(k<3)
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage-(ik-k-2))+");load_data_a();>"+(curPage-(ik-k-2))+"</a></li>";
					}
					else if(k==3)
					{
						html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_a').val("+curPage+");load_data_a();>"+curPage+"</a></li>";
					}
					else
					{
						html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage+(k-3))+");load_data_a();>"+(curPage+(k-3))+"</a></li>";
					} 
				}
			}
			html_li+="<li class='next' id='b'><a   href='javascript:void(0)' onclick=$('#cupage_a').val(parseInt($('#cupage_a').val())+1);load_data_a();>Trang sau</a><a href='javascript:void(0)' onclick =$('#cupage_a').val("+total+");load_data_a();>Trang cuối </a></li></ul>";
		  }
		  if(curPage==total)
		  {
			  html_li += "<li class='prev' id='befora'> <a href='javascript:void(0)' onclick =$('#cupage_a').val(1);load_data_a();>Trang đầu</a><a href='javascript:void(0)' onclick =$('#cupage_a').val(parseInt($('#cupage_a').val())-1);load_data_a();>Trang trước</a></li>";   
			  for(var k=1;k<=ik;k++)
			  {
				if(k==ik)
				{
					html_li +="<li class='active'><a href='javascript:void(0)' onclick=$('#cupage_a').val("+curPage+");load_data_a();>"+curPage+"</a></li>";
				}
				else
				{
					html_li +="<li><a href='javascript:void(0)' onclick=$('#cupage_a').val("+(curPage-(ik-k))+");load_data_a();>"+(curPage-(ik-k))+"</a></li>";
				}
			  }
			  html_li+="<li class='next disabled' id='b'><a href='javascript:void(0)'>Trang sau</a><a href='javascript:void(0)'>Trang cuối </a></li></ul>";
		 }
		 return html_li;
   }
   
   /*
     为分页赋值
   */
   function page_value_a(data)
	{
		$("#totalrows_a").html(data.list.totalRows);
		$("#numperpage_a").html(data.list.numPerPage);
		$("#currentpage_a").html(data.list.currentPage);
		$("#totalpages_a").html(data.list.totalPages);
		$("#cupage_a").val(data.list.currentPage);
		$("#total_a").val(data.list.totalPages);
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
   
   
   