// JavaScript Document


//province(省份类)
function provinceList()
{
    this.length=35;
	this[-1] = new Option("请选择","-1");
    this[0] = new Option("安徽","0");
    this[1] = new Option("北京","1");
    this[2] = new Option("重庆","2");
    this[3] = new Option("福建","3");
    this[4] = new Option("甘肃","4");
    this[5] = new Option("广东","5");
    this[6] = new Option("广西","6");
    this[7] = new Option("贵州","7");
    this[8] = new Option("海南","8");
    this[9] = new Option("河北","9");
    this[10] = new Option("河南","10");
    this[11] = new Option("黑龙江","11");
    this[12] = new Option("湖北","12");
    this[13] = new Option("湖南","13");
    this[14] = new Option("江苏","14");
    this[15] = new Option("江西","15");
    this[16] = new Option("吉林","16");
    this[17] = new Option("辽宁","17");
    this[18] = new Option("内蒙古","18");
    this[19] = new Option("宁夏","19");
    this[20] = new Option("青海","20");
    this[21] = new Option("上海","21");
    this[22] = new Option("山东","22");
    this[23] = new Option("山西","23");
    this[24] = new Option("陕西","24");
    this[25] = new Option("四川","25");
    this[26] = new Option("天津","26");
    this[27] = new Option("新疆","27");
    this[28] = new Option("西藏","28");
    this[29] = new Option("云南","29");
    this[30] = new Option("浙江","30");
    this[31] = new Option("香港","31");
    this[32] = new Option("澳门","32");
    this[33] = new Option("台湾","33");
    this[34] = new Option("其它","34");
    return this;
}

//city(城类)
function citylist()
{
    this.length=35;
    this[-1] = new Array(0);
    this[-1][-1] = new Option("请选择","-1");
    this[0] = new Array(17);
	this[0][-1] = new Option("请选择","-1");
    this[0][0] = new Option("合肥","0");
    this[0][1] = new Option("淮北","1");
    this[0][2] = new Option("淮南","2");
    this[0][3] = new Option("黄山","3");
    this[0][4] = new Option("安庆","4");
    this[0][5] = new Option("蚌埠","5");
    this[0][6] = new Option("巢湖","6");
    this[0][7] = new Option("池州","7");
    this[0][8] = new Option("滁州","8");
    this[0][9] = new Option("六安","9");
    this[0][10] = new Option("马鞍山","10");
    this[0][11] = new Option("宣城","11");
    this[0][12] = new Option("宿州","12");
    this[0][13] = new Option("铜陵","13");
    this[0][14] = new Option("芜湖","14");
    this[0][15] = new Option("阜阳","15");
    this[0][16] = new Option("亳州","16");

    this[1] = new Array(1);
	this[1][-1] = new Option("请选择","-1");
    this[1][0] = new Option("北京","100");
	
	
    this[2] = new Array(4);
	this[2][-1] = new Option("请选择","-1");
    this[2][0] = new Option("重庆","200");
    this[2][1] = new Option("涪陵","201");
    this[2][2] = new Option("黔江","202");
    this[2][3] = new Option("万县","203");
	
    this[3] = new Array(9);
	this[3][-1] = new Option("请选择","-1");
    this[3][0] = new Option("福州","300");
    this[3][1] = new Option("龙岩","301");
    this[3][2] = new Option("南平","302");
    this[3][3] = new Option("宁德","303");
    this[3][4] = new Option("莆田","304");
    this[3][5] = new Option("泉州","305");
    this[3][6] = new Option("三明","306");
    this[3][7] = new Option("厦门","307");
    this[3][8] = new Option("漳州","308");
	
    this[4] = new Array(14);
	this[4][-1] = new Option("请选择","-1");
    this[4][0] = new Option("兰州","400");
    this[4][1] = new Option("甘南藏族自治州","401");
    this[4][2] = new Option("定西地区","402");
    this[4][3] = new Option("白银","403");
    this[4][4] = new Option("嘉峪关","404");
    this[4][5] = new Option("金昌","405");
    this[4][6] = new Option("酒泉","406");
    this[4][7] = new Option("临夏回族自治州","407");
    this[4][8] = new Option("陇南地区","408");
    this[4][9] = new Option("平凉","409");
    this[4][10] = new Option("庆阳","410");
    this[4][11] = new Option("天水","411");
    this[4][12] = new Option("武威","412");
    this[4][13] = new Option("张掖","413");
	
    this[5] = new Array(21);
	this[5][-1] = new Option("请选择","-1");
    this[5][0] = new Option("广州","500");
    this[5][1] = new Option("佛山","501");
    this[5][2] = new Option("惠州","502");
    this[5][3] = new Option("东莞","503");
    this[5][4] = new Option("江门","504");
    this[5][5] = new Option("揭阳","505");
    this[5][6] = new Option("潮州","506");
    this[5][7] = new Option("茂名","507");
    this[5][8] = new Option("梅州","508");
    this[5][9] = new Option("清远","509");
    this[5][10] = new Option("汕头","510");
    this[5][11] = new Option("汕尾","511");
    this[5][12] = new Option("深圳","512");
    this[5][13] = new Option("韶关","513");
    this[5][14] = new Option("阳江","514");
    this[5][15] = new Option("河源","515");
    this[5][16] = new Option("云浮","516");
    this[5][17] = new Option("中山","517");
    this[5][18] = new Option("珠海","518");
    this[5][19] = new Option("湛江","519");
    this[5][20] = new Option("肇庆","520");
	
    this[6] = new Array(14);
	this[6][-1] = new Option("请选择","-1");
    this[6][0] = new Option("南宁","600");
    this[6][1] = new Option("防城港","601");
    this[6][2] = new Option("北海","602");
    this[6][3] = new Option("百色","603");
    this[6][4] = new Option("贺州","604");
    this[6][5] = new Option("桂林","605");
    this[6][6] = new Option("来宾","606");
    this[6][7] = new Option("柳州","607");
    this[6][8] = new Option("崇左","608");
    this[6][9] = new Option("钦州","609");
    this[6][10] = new Option("贵港","610");
    this[6][11] = new Option("梧州","611");
    this[6][12] = new Option("河池","612");
    this[6][13] = new Option("玉林","613");
	
    this[7] = new Array(9);
	this[7][-1] = new Option("请选择","-1");
    this[7][0] = new Option("贵阳","700");
    this[7][1] = new Option("毕节地区","701");
    this[7][2] = new Option("遵义","702");
    this[7][3] = new Option("安顺","703");
    this[7][4] = new Option("六盘水","704");
    this[7][5] = new Option("黔东南苗族侗族自治州","705");
    this[7][6] = new Option("黔南布依族苗族自治州","706");
    this[7][7] = new Option("黔西南布依族苗族自治","707");
    this[7][8] = new Option("铜仁地区","708");
	
    this[8] = new Array(3);
	this[8][-1] = new Option("请选择","-1");
    this[8][0] = new Option("海口","800");
    this[8][1] = new Option("三亚","801");
    this[8][2] = new Option("省直辖行政单位","802");
	
    this[9] = new Array(11);
	this[9][-1] = new Option("请选择","-1");
    this[9][0] = new Option("石家庄","900");
    this[9][1] = new Option("邯郸","901");
    this[9][2] = new Option("邢台","902");
    this[9][3] = new Option("保定","903");
    this[9][4] = new Option("张家口","904");
    this[9][5] = new Option("沧州","905");
    this[9][6] = new Option("承德","906");
    this[9][7] = new Option("廊坊","907");
    this[9][8] = new Option("秦皇岛","908");
    this[9][9] = new Option("唐山","909");
    this[9][10] = new Option("衡水","910");
	
    this[10] = new Array(18);
	this[10][-1] = new Option("请选择","-1");
    this[10][0] = new Option("郑州","1000");
    this[10][1] = new Option("开封","1001");
    this[10][2] = new Option("驻马店","1002");
    this[10][3] = new Option("安阳","1003");
    this[10][4] = new Option("焦作","1004");
    this[10][5] = new Option("洛阳","1005");
    this[10][6] = new Option("濮阳","1006");
    this[10][7] = new Option("漯河","1007");
    this[10][8] = new Option("南阳","1008");
    this[10][9] = new Option("平顶山","1009");
    this[10][10] = new Option("新乡","1010");
    this[10][11] = new Option("信阳","1011");
    this[10][12] = new Option("许昌","1012");
    this[10][13] = new Option("商丘","1013");
    this[10][14] = new Option("三门峡","1014");
    this[10][15] = new Option("鹤壁","1015");
    this[10][16] = new Option("周口","1016");
    this[10][17] = new Option("济源","1017");

    this[11] = new Array(13);
	this[11][-1] = new Option("请选择","-1");
    this[11][0] = new Option("哈尔滨","1100");
    this[11][1] = new Option("大庆","1101");
    this[11][2] = new Option("大兴安岭地区","1102");
    this[11][3] = new Option("鸡西","1103");
    this[11][4] = new Option("佳木斯","1104");
    this[11][5] = new Option("牡丹江","1105");
    this[11][6] = new Option("齐齐哈尔","1106");
    this[11][7] = new Option("七台河","1107");
    this[11][8] = new Option("双鸭山","1108");
    this[11][9] = new Option("绥化","1109");
    this[11][10] = new Option("伊春","1110");
    this[11][11] = new Option("鹤岗","1111");
    this[11][12] = new Option("黑河","1112");

    this[12] = new Array(14);
	this[12][-1] = new Option("请选择","-1");
    this[12][0] = new Option("武汉","1200");
    this[12][1] = new Option("黄冈","1201");
    this[12][2] = new Option("黄石","1202");
    this[12][3] = new Option("恩施土家族苗族自治州","1203");
    this[12][4] = new Option("鄂州","1204");
    this[12][5] = new Option("荆门","1205");
    this[12][6] = new Option("荆州","1206");
    this[12][7] = new Option("孝感","1207");
    this[12][8] = new Option("省直辖县级行政单位","1208");
    this[12][9] = new Option("十堰","1209");
    this[12][10] = new Option("襄樊","1210");
    this[12][11] = new Option("咸宁","1211");
    this[12][12] = new Option("宜昌","1212");
    this[12][13] = new Option("随州","1213");
	
    this[13] = new Array(14);
	this[13][-1] = new Option("请选择","-1");
    this[13][0] = new Option("长沙","1300");
    this[13][1] = new Option("怀化","1301");
    this[13][2] = new Option("郴州","1302");
    this[13][3] = new Option("常德","1303");
    this[13][4] = new Option("娄底","1304");
    this[13][5] = new Option("邵阳","1305");
    this[13][6] = new Option("湘潭","1306");
    this[13][7] = new Option("湘西土家族苗族自治州","1307");
    this[13][8] = new Option("衡阳","1308");
    this[13][9] = new Option("永州","1309");
    this[13][10] = new Option("益阳","1310");
    this[13][11] = new Option("岳阳","1311");
    this[13][12] = new Option("株洲","1312");
    this[13][13] = new Option("张家界","1313");
	
    this[14] = new Array(13);
	this[14][-1] = new Option("请选择","-1");
    this[14][0] = new Option("南京","1400");
    this[14][1] = new Option("淮安","1401");
    this[14][2] = new Option("常州","1402");
    this[14][3] = new Option("连云港","1403");
    this[14][4] = new Option("南通","1404");
    this[14][5] = new Option("徐州","1405");
    this[14][6] = new Option("苏州","1406");
    this[14][7] = new Option("无锡","1407");
    this[14][8] = new Option("盐城","1408");
    this[14][9] = new Option("扬州","1409");
    this[14][10] = new Option("镇江","1410");
    this[14][11] = new Option("泰州","1411");
    this[14][12] = new Option("宿迁","1412");
	
    this[15] = new Array(11);
	this[15][-1] = new Option("请选择","-1");
    this[15][0] = new Option("南昌","1500");
    this[15][1] = new Option("抚州","1501");
    this[15][2] = new Option("赣州","1502");
    this[15][3] = new Option("吉安","1503");
    this[15][4] = new Option("景德镇","1504");
    this[15][5] = new Option("九江","1505");
    this[15][6] = new Option("萍乡","1506");
    this[15][7] = new Option("新余","1507");
    this[15][8] = new Option("上饶","1508");
    this[15][9] = new Option("鹰潭","1509");
    this[15][10] = new Option("宜春","1510");
	
    this[16] = new Array(9);
	this[16][-1] = new Option("请选择","-1");
    this[16][0] = new Option("长春","1600");
    this[16][1] = new Option("白城","1601");
    this[16][2] = new Option("白山","1602");
    this[16][3] = new Option("吉林","1603");
    this[16][4] = new Option("辽源","1604");
    this[16][5] = new Option("四平","1605");
    this[16][6] = new Option("松原","1606");
    this[16][7] = new Option("通化","1607");
    this[16][8] = new Option("延边朝鲜族自治州","1608");
	
    this[17] = new Array(14);
	this[17][-1] = new Option("请选择","-1");
    this[17][0] = new Option("沈阳","1700");
    this[17][1] = new Option("大连","1701");
    this[17][2] = new Option("阜新","1702");
    this[17][3] = new Option("抚顺","1703");
    this[17][4] = new Option("本溪","1704");
    this[17][5] = new Option("鞍山","1705");
    this[17][6] = new Option("丹东","1706");
    this[17][7] = new Option("锦州","1707");
    this[17][8] = new Option("朝阳","1708");
    this[17][9] = new Option("辽阳","1709");
    this[17][10] = new Option("盘锦","1710");
    this[17][11] = new Option("铁岭","1711");
    this[17][12] = new Option("营口","1712");
    this[17][13] = new Option("葫芦岛","1713");
	
    this[18] = new Array(12);
	this[18][-1] = new Option("请选择","-1");
    this[18][0] = new Option("呼和浩特","1800");
    this[18][1] = new Option("阿拉善盟","1801");
    this[18][2] = new Option("巴彦淖尔盟","1802");
    this[18][3] = new Option("包头","1803");
    this[18][4] = new Option("赤峰","1804");
    this[18][5] = new Option("兴安盟","1805");
    this[18][6] = new Option("乌兰察布盟","1806");
    this[18][7] = new Option("乌海","1807");
    this[18][8] = new Option("锡林郭勒盟","1808");
    this[18][9] = new Option("呼伦贝尔盟","1809");
    this[18][10] = new Option("伊克昭盟","1810");
    this[18][11] = new Option("通辽","1811");
	
    this[19] = new Array(5);
	this[19][-1] = new Option("请选择","-1");
    this[19][0] = new Option("银川","1900");
    this[19][1] = new Option("固原","1901");
    this[19][2] = new Option("石嘴山","1902");
    this[19][3] = new Option("吴忠","1903");
    this[19][4] = new Option("中卫","1904");
	
    this[20] = new Array(8);
	this[20][-1] = new Option("请选择","-1");
    this[20][0] = new Option("西宁","2000");
    this[20][1] = new Option("海东地区","2001");
    this[20][2] = new Option("海南藏族自治州","2002");
    this[20][3] = new Option("海北藏族自治州","2003");
    this[20][4] = new Option("黄南藏族自治州","2004");
    this[20][5] = new Option("果洛藏族自治州","2005");
    this[20][6] = new Option("玉树藏族自治州","2006");
    this[20][7] = new Option("海西蒙古族藏族自治州","2007");
	
    this[21] = new Array(1);
	this[21][-1] = new Option("请选择","-1");
    this[21][0] = new Option("上海","2100");
	
    this[22] = new Array(17);
	this[22][-1] = new Option("请选择","-1");
    this[22][0] = new Option("济南","2200");
    this[22][1] = new Option("东营","2201");
    this[22][2] = new Option("滨州","2202");
    this[22][3] = new Option("淄博","2203");
    this[22][4] = new Option("德州","2204");
    this[22][5] = new Option("济宁","2205");
    this[22][6] = new Option("聊城","2206");
    this[22][7] = new Option("临沂","2207");
    this[22][8] = new Option("莱芜","2208");
    this[22][9] = new Option("青岛","2209");
    this[22][10] = new Option("日照","2210");
    this[22][11] = new Option("威海","2211");
    this[22][12] = new Option("泰安","2212");
    this[22][13] = new Option("潍坊","2213");
    this[22][14] = new Option("烟台","2214");
    this[22][15] = new Option("菏泽","2215");
    this[22][16] = new Option("枣庄","2216");
	
    this[23] = new Array(11);
	this[23][-1] = new Option("请选择","-1");
    this[23][0] = new Option("太原","2300");
    this[23][1] = new Option("大同","2301");
    this[23][2] = new Option("晋城","2302");
    this[23][3] = new Option("晋中","2303");
    this[23][4] = new Option("长治","2304");
    this[23][5] = new Option("临汾","2305");
    this[23][6] = new Option("吕梁地区","2306");
    this[23][7] = new Option("忻州","2307");
    this[23][8] = new Option("朔州","2308");
    this[23][9] = new Option("阳泉","2309");
    this[23][10] = new Option("运城","2310");
	
    this[24] = new Array(10);
	this[24][-1] = new Option("请选择","-1");
    this[24][0] = new Option("西安","2400");
    this[24][1] = new Option("宝鸡","2401");
    this[24][2] = new Option("安康","2402");
    this[24][3] = new Option("商洛","2403");
    this[24][4] = new Option("铜川","2404");
    this[24][5] = new Option("渭南","2405");
    this[24][6] = new Option("咸阳","2406");
    this[24][7] = new Option("延安","2407");
    this[24][8] = new Option("汉中","2408");
    this[24][9] = new Option("榆林","2409");
	
    this[25] = new Array(21);
	this[25][-1] = new Option("请选择","-1");
    this[25][0] = new Option("成都","2500");
    this[25][1] = new Option("达川","2501");
    this[25][2] = new Option("甘孜藏族自治州","2502");
    this[25][3] = new Option("自贡","2503");
    this[25][4] = new Option("阿坝藏族羌族自治州","2504");
    this[25][5] = new Option("巴中","2505");
    this[25][6] = new Option("德阳","2506");
    this[25][7] = new Option("广安","2507");
    this[25][8] = new Option("广元","2508");
    this[25][9] = new Option("凉山彝族自治州","2509");
    this[25][10] = new Option("乐山","2510");
    this[25][11] = new Option("攀枝花","2511");
    this[25][12] = new Option("南充","2512");
    this[25][13] = new Option("内江","2513");
    this[25][14] = new Option("泸州","2514");
    this[25][15] = new Option("绵阳","2515");
    this[25][16] = new Option("遂宁","2516");
    this[25][17] = new Option("雅安","2517");
    this[25][18] = new Option("宜宾","2518");
    this[25][19] = new Option("眉山","2519");
    this[25][20] = new Option("资阳","2520");
	
    this[26] = new Array(1);
	this[26][-1] = new Option("请选择","-1");
    this[26][0] = new Option("天津","2600");
	
    this[27] = new Array(15);
	this[27][-1] = new Option("请选择","-1");
    this[27][0] = new Option("乌鲁木齐","2700");
    this[27][1] = new Option("喀什地区","2701");
    this[27][2] = new Option("克孜勒苏柯尔克孜自治","2702");
    this[27][3] = new Option("克拉玛依","2703");
    this[27][4] = new Option("阿克苏地区","2704");
    this[27][5] = new Option("阿勒泰地区","2705");
    this[27][6] = new Option("巴音郭楞蒙古自治州","2706");
    this[27][7] = new Option("哈密地区","2707");
    this[27][8] = new Option("博尔塔拉蒙古自治州","2708");
    this[27][9] = new Option("昌吉回族自治州","2709");
    this[27][10] = new Option("塔城地区","2710");
    this[27][11] = new Option("吐鲁番地区","2711");
    this[27][12] = new Option("和田地区","2712");
    this[27][13] = new Option("石河子","2713");
    this[27][14] = new Option("伊犁哈萨克自治州","2714");
	
    this[28] = new Array(7);
	this[28][-1] = new Option("请选择","-1");
    this[28][0] = new Option("拉萨","2800");
    this[28][1] = new Option("阿里地区","2801");
    this[28][2] = new Option("昌都","2802");
    this[28][3] = new Option("林芝地区","2803");
    this[28][4] = new Option("那曲地区","2804");
    this[28][5] = new Option("山南地区","2805");
    this[28][6] = new Option("日喀则地区","2806");
	
    this[29] = new Array(16);
	this[29][-1] = new Option("请选择","-1");
    this[29][0] = new Option("昆明","2900");
    this[29][1] = new Option("大理白族自治州","2901");
    this[29][2] = new Option("昭通","2902");
    this[29][3] = new Option("保山","2903");
    this[29][4] = new Option("德宏傣族景颇族自治州","2904");
    this[29][5] = new Option("迪庆藏族自治州","2905");
    this[29][6] = new Option("楚雄彝族自治州","2906");
    this[29][7] = new Option("临沧地区","2907");
    this[29][8] = new Option("丽江","2908");
    this[29][9] = new Option("怒江傈僳族自治州","2909");
    this[29][10] = new Option("曲靖","2910");
    this[29][11] = new Option("思茅地区","2911");
    this[29][12] = new Option("西双版纳傣族自治州","2912");
    this[29][13] = new Option("文山壮族苗族自治州","2913");
    this[29][14] = new Option("红河哈尼族彝族自治州","2914");
    this[29][15] = new Option("玉溪","2915");
	
    this[30] = new Array(11);
	this[30][-1] = new Option("请选择","-1");
    this[30][0] = new Option("杭州","3000");
    this[30][1] = new Option("嘉兴","3001");
    this[30][2] = new Option("金华","3002");
    this[30][3] = new Option("衢州","3003");
    this[30][4] = new Option("丽水","3004");
    this[30][5] = new Option("宁波","3005");
    this[30][6] = new Option("绍兴","3006");
    this[30][7] = new Option("台州","3007");
    this[30][8] = new Option("温州","3008");
    this[30][9] = new Option("湖州","3009");
    this[30][10] = new Option("舟山","3010");
	
    this[31] = new Array(1);
	this[31][-1] = new Option("请选择","-1");
    this[31][0] = new Option("香港","3100");
    this[32] = new Array(1);
	this[32][-1] = new Option("请选择","-1");
    this[32][0] = new Option("澳门","3200");
    this[33] = new Array(1);
	this[33][-1] = new Option("请选择","-1");
    this[33][0] = new Option("台湾","3300");
	
    this[34] = new Array(7);
	this[34][-1] = new Option("请选择","-1");
    this[34][0] = new Option("北美洲","3400");
    this[34][1] = new Option("南美洲","3401");
    this[34][2] = new Option("大洋洲","3402");
    this[34][3] = new Option("欧洲","3403");
    this[34][4] = new Option("亚洲","3404");
    this[34][5] = new Option("非洲","3405");
    this[34][6] = new Option("虚拟社团","3406");
    return this;
}

//创建provincelist、citylist类实例
var provinceOb=new provinceList();
var cityOb=new citylist();

//定义province、city变量，用于select元素
var province;
var city;

//子函数添加城
function addCitys(province,city)
{
    var index=province.selectedIndex;
    for(var i=-1;i<cityOb[index-1].length;i++)
    {
        try
        {
            city.add(cityOb[index-1][i]);
        }
        catch(e)
        {
            city.add(cityOb[index][i],null);
        }
    }
}

//子函数删除城
function delCitys(city)
{
//    for(var i=0;i<city.length;i++)
//    {
//        city.remove(i);
//    }
    city.length=0;
}


//初始化地区下拉菜单
function initialize(privinceId,cityId)
{
    //获取select元素
    province=document.getElementById("province");
    city=document.getElementById("city");
    
    //循环添加省份到province
    for(var i=-1;i<provinceOb.length;i++)
    {
        try
        {
            province.add(provinceOb[i]);
        }
        catch(e)
        {
            province.add(provinceOb[i],null);
        }
     }
        
        //初始化privinceId
        if(privinceId==undefined)
        {
            privinceId=0;
        }
        //设置province默认选项
        province.options[privinceId].selected=true;
        
        //添加城到city
        addCitys(province,city);
        //设置city的默认选项
        if(cityId!=undefined)
        {
            city.options[cityId].selected=true;
        }
        else
        {  
            city.options[0].selected=true;
        }        
}

  //下拉列表改变事件
    function selectchange(province,city)
    {
        delCitys(city);
        addCitys(province,city);
    }