package io.renren.modules.jxgk.utils;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeCreater {
        private String FilePath;//文件路径
        private String EntityName;//实体名称
        private String LowEntityName;//实体首字母小写名称
        private int EntityPropertyNum;//实体字段数量
        private String DataBaseName;//数据库表名称
        private String [] EntityProperty;//实体字段数组
        private String [] EntityChineseProperty;//实体中文名数组
        private String [] EntityPropertyUpper;//实体首字母大写数组
        private String [] EntityPropertyCamel;//实体驼峰转下划线数组
        private String Key;//主键
        private String KeyCamel;//主键驼峰转下划线
        private String SearchKey;//搜索键
        private String SearchKeyCamel;//搜索键驼峰转下划线
        private int SearchKeyNum;//搜索键在实体数组中的位置
        public CodeCreater(String FilePath,String[] EntityChineseProperty,int SearchKeyNum,String EntityName,String [] EntityProperty,int EntityPropertyNum){
            this.FilePath=FilePath;
            this.EntityName=EntityName;
            this.EntityPropertyNum=EntityPropertyNum;
            this.EntityProperty=EntityProperty;
            this.DataBaseName=Camel2Underline(EntityName);
            this.LowEntityName=FLower(EntityName);
            this.EntityChineseProperty=EntityChineseProperty;
            this.EntityPropertyUpper=new String[EntityPropertyNum];
            this.EntityPropertyCamel=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                EntityPropertyUpper[i]=FUpper(EntityProperty[i]);
            }
            for(int i=0;i<EntityPropertyNum;i++){
                EntityPropertyCamel[i]=Camel2Underline(EntityProperty[i]);
            }
            this.Key=EntityProperty[0];
            this.KeyCamel=EntityPropertyCamel[0];
            this.SearchKey=EntityProperty[SearchKeyNum];
            this.SearchKeyCamel=EntityPropertyCamel[SearchKeyNum];
            this.SearchKeyNum=SearchKeyNum;
        }
        public String FUpper(String origin){//首字母大写
            char[] cs=origin.toCharArray();
            cs[0]-=32;
            return String.valueOf(cs);
        }
        public String FLower(String origin){//首字母小写
            char[] cs=origin.toCharArray();
            cs[0]+=32;
            return String.valueOf(cs);
        }
        public String Camel2Underline(String line){//驼峰转下划线
            if(line==null||"".equals(line)){
                return "";
            }
            line=String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
            StringBuffer sb=new StringBuffer();
            Pattern pattern=Pattern.compile("[A-Z]([a-z\\d]+)?");
            Matcher matcher=pattern.matcher(line);
            while(matcher.find()){
                String word=matcher.group();
                sb.append(word.toUpperCase());
                sb.append(matcher.end()==line.length()?"":"_");
            }
            return sb.toString().toLowerCase();
        }
        public void FileWriter(String content,String path,String name){//后端文件写入
            FileWriter fw = null;
            try {
                fw = new FileWriter(FilePath +
                        "java\\io\\renren\\modules\\jxgk\\"+path+"\\"+EntityName+""+name+".java");
                fw.write(content);
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void FileWriterWeb(String content,String path,String name,String type){//前端文件写入
        FileWriter fw = null;
        try {
            fw = new FileWriter(FilePath +
                    "resources\\"+path+"\\jxgk\\"+name+"."+type+"");
            fw.write(content);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public void CreateEntityCode(){
            String [] Initialize=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                Initialize[i]="    private String "+EntityProperty[i]+";\n" ;
            }
            String Initial="";
            for(int i=0;i<EntityPropertyNum;i++){
                Initial+=Initialize[i];
            }
            String [] SetterGetter=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                SetterGetter[i]= "    public String get"+EntityPropertyUpper[i]+"() {\n" +
                        "        return "+EntityProperty[i]+";\n" +
                        "    }\n" +
                        "\n" +
                        "    public void set"+EntityPropertyUpper[i]+"(String "+EntityProperty[i]+") {\n" +
                        "        this."+EntityProperty[i]+" = "+EntityProperty[i]+";\n" +
                        "    }\n" +
                        "\n" ;
            }
            String GetSet="";
            for(int i=0;i<EntityPropertyNum;i++){
                GetSet+=SetterGetter[i];
            }
            String EntityCode="package io.renren.modules.jxgk.entity;\n" +
                    "\n" +
                    "public class "+EntityName+"Entity {\n" +
                    Initial+
                    "\n" +
                    GetSet+
                    "}\n";
            FileWriter(EntityCode,"entity","Entity");
        }
        public void CreateControllerCode(){
            String ControllerCode="package io.renren.modules.jxgk.controller;\n" +
                    "\n" +
                    "import io.renren.common.annotation.SysLog;\n" +
                    "import io.renren.common.utils.PageUtils;\n" +
                    "import io.renren.common.utils.Query;\n" +
                    "import io.renren.common.utils.R;\n" +
                    "import io.renren.modules.jxgk.entity."+EntityName+"Entity;\n" +
                    "import io.renren.modules.jxgk.service."+EntityName+"Service;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.web.bind.annotation.*;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "\n" +
                    "@RestController\n" +
                    "@RequestMapping(\""+EntityName+"/\")\n" +
                    "public class "+EntityName+"Controller {\n" +
                    "\n" +
                    "    @Autowired\n" +
                    "    "+EntityName+"Service "+LowEntityName+"Service;\n" +
                    "\n" +
                    "    @RequestMapping(\"Select\")\n" +
                    "    public R list(@RequestParam Map<String, Object> params){\n" +
                    "        //查询列表数据\n" +
                    "        Query query = new Query(params);\n" +
                    "        List<"+EntityName+"Entity> cse = "+LowEntityName+"Service.queryList(query);\n" +
                    "        int total = "+LowEntityName+"Service.queryTotal(query);\n" +
                    "\n" +
                    "        PageUtils pageUtil = new PageUtils(cse, total, query.getLimit(), query.getPage());\n" +
                    "\n" +
                    "        return R.ok().put(\"page\", pageUtil);\n" +
                    "    }\n" +
                    "    @RequestMapping(\"/Delete\")\n" +
                    "    public R delete(@RequestBody String[] indexs){\n" +
                    "        "+LowEntityName+"Service.deleteBatch(indexs);\n" +
                    "        return R.ok();\n" +
                    "    }\n" +
                    "    @RequestMapping(\"/Insert\")\n" +
                    "    public R save(@RequestBody "+EntityName+"Entity ste){\n" +
                    "        "+LowEntityName+"Service.save(ste);\n" +
                    "        return R.ok();\n" +
                    "    }\n" +
                    "    @RequestMapping(\"/Info/{index}\")\n" +
                    "    public R info(@PathVariable(\"index\") String index){\n" +
                    "        "+EntityName+"Entity cse = "+LowEntityName+"Service.queryObject(index);\n" +
                    "        return R.ok().put(\""+EntityName+"\", cse);\n" +
                    "    }\n" +
                    "    @SysLog(\"修改用户\")\n" +
                    "    @RequestMapping(\"/Update\")\n" +
                    "    public R update(@RequestBody "+EntityName+"Entity cse){\n" +
                    "        "+LowEntityName+"Service.update(cse);\n" +
                    "        return R.ok();\n" +
                    "    }\n" +
                    "}";
            FileWriter(ControllerCode,"controller","Controller");
        }
        public void CreateDaoCode(){
            String DaoCode="package io.renren.modules.jxgk.dao;\n" +
                    "\n" +
                    "import io.renren.modules.jxgk.entity."+EntityName+"Entity;\n" +
                    "import io.renren.modules.sys.dao.BaseDao;\n" +
                    "import org.apache.ibatis.annotations.Mapper;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "\n" +
                    "@Repository\n" +
                    "@Mapper\n" +
                    "public interface "+EntityName+"Dao extends BaseDao<"+EntityName+"Entity>{\n" +
                    "}\n";
            FileWriter(DaoCode,"dao","Dao");
        }
        public void CreateServiceCode(){
            String ServiceCode="package io.renren.modules.jxgk.service;\n" +
                    "\n" +
                    "import io.renren.modules.jxgk.entity."+EntityName+"Entity;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "public interface "+EntityName+"Service {\n" +
                    "    List<"+EntityName+"Entity> queryList(Map<String, Object> map);\n" +
                    "    int queryTotal(Map<String, Object> map);\n" +
                    "    void deleteBatch(String[] indexs);\n" +
                    "    void save("+EntityName+"Entity cse);\n" +
                    "    "+EntityName+"Entity queryObject(String index);\n" +
                    "    void update("+EntityName+"Entity cse);\n" +
                    "}\n";
            FileWriter(ServiceCode,"service","Service");
        }
        public void CreateServiceImplCode(){
            String ImplCode="package io.renren.modules.jxgk.service.impl;\n" +
                    "\n" +
                    "import io.renren.modules.jxgk.dao."+EntityName+"Dao;\n" +
                    "import io.renren.modules.jxgk.entity."+EntityName+"Entity;\n" +
                    "import io.renren.modules.jxgk.service."+EntityName+"Service;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Service;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "import java.util.Map;\n" +
                    "\n" +
                    "@Service\n" +
                    "public class "+EntityName+"ServiceImpl implements "+EntityName+"Service{\n" +
                    "    @Autowired\n" +
                    "    "+EntityName+"Dao "+LowEntityName+"Dao;\n" +
                    "    public List<"+EntityName+"Entity> queryList(Map<String, Object> map){\n" +
                    "        return "+LowEntityName+"Dao.queryList(map);\n" +
                    "    }\n" +
                    "    public int queryTotal(Map<String, Object> map){\n" +
                    "        return  "+LowEntityName+"Dao.queryTotal(map);\n" +
                    "    }\n" +
                    "    public void deleteBatch(String[] termIds){\n" +
                    "        "+LowEntityName+"Dao.deleteBatch(termIds);\n" +
                    "    }\n" +
                    "    public void save("+EntityName+"Entity ste){\n" +
                    "        "+LowEntityName+"Dao.save(ste);\n" +
                    "    }\n" +
                    "    public "+EntityName+"Entity queryObject(String index){\n" +
                    "        return "+LowEntityName+"Dao.queryObject(index);\n" +
                    "    }\n" +
                    "    public void update("+EntityName+"Entity ste){\n" +
                    "        "+LowEntityName+"Dao.update(ste);\n" +
                    "    }\n" +
                    "}\n";
            FileWriter(ImplCode,"service\\impl","ServiceImpl");
        }
        public void CreateMapperCode(){
            String [] InsertContent=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                if(i==EntityPropertyNum-1){
                    InsertContent[i]="        `"+EntityPropertyCamel[i]+"`\n ";
                }else {
                    InsertContent[i]="        `"+EntityPropertyCamel[i]+"`,\n ";
                }
            }
            String Insert="";
            for(int i=0;i<EntityPropertyNum;i++){
               Insert+=InsertContent[i];
            }
            String [] InsertContent2=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                if(i==EntityPropertyNum-1){
                    InsertContent2[i]="        #{"+EntityProperty[i]+"}\n ";
                }else {
                    InsertContent2[i]="        #{"+EntityProperty[i]+"},\n ";
                }

            }
            String Insert2="";
            for(int i=0;i<EntityPropertyNum;i++){
                Insert2+=InsertContent2[i];
            }
            String [] UpdateContent=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                if(EntityProperty[i].equals(SearchKey)){
                    UpdateContent[i]="";
                } else {
                    UpdateContent[i]="            <if test=\""+EntityProperty[i]+" != null\">`"+EntityPropertyCamel[i]+"` = #{"+EntityProperty[i]+"}, </if>\n ";
                }
            }
            String Update="";
            for(int i=0;i<EntityPropertyNum;i++){
                Update+=UpdateContent[i];
            }
            String MapperCode="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                    "\n" +
                    "<mapper namespace=\"io.renren.modules.jxgk.dao."+EntityName+"Dao\">\n" +
                    "    <insert id=\"save\" parameterType=\"io.renren.modules.jxgk.entity."+EntityName+"Entity\">\n" +
                    "        insert into "+DataBaseName+"\n" +
                    "        (\n" +
                             Insert+
                    "        )\n" +
                    "        values\n" +
                    "        (\n" +
                             Insert2+
                    "        )\n" +
                    "    </insert>\n" +
                    "    <select id=\"queryObject\" resultType=\"io.renren.modules.jxgk.entity."+EntityName+"Entity\">\n" +
                    "        select * from "+DataBaseName+" where "+KeyCamel+" = #{"+Key+"}\n" +
                    "    </select>\n" +
                    "    <select id=\"queryList\" resultType=\"io.renren.modules.jxgk.entity."+EntityName+"Entity\">\n" +
                    "        select * from "+DataBaseName+"\n" +
                    "        <where>\n" +
                    "            <if test=\""+SearchKey+" != null and "+SearchKey+".trim() != ''\">\n" +
                    "                and `"+SearchKeyCamel+"` like concat('%',#{"+SearchKey+"},'%')\n" +
                    "            </if>\n" +
                    "        </where>\n" +
                    "        <if test=\"offset != null and limit != null\">\n" +
                    "            limit #{offset}, #{limit}\n" +
                    "        </if>\n" +
                    "    </select>\n" +
                    "    <select id=\"queryTotal\" resultType=\"int\">\n" +
                    "        select count(*) from "+DataBaseName+"\n" +
                    "    </select>\n" +
                    "    <delete id=\"deleteBatch\">\n" +
                    "        delete from "+DataBaseName+" where "+KeyCamel+" in\n" +
                    "        <foreach item=\"termIds\" collection=\"array\" open=\"(\" separator=\",\" close=\")\">\n" +
                    "            #{termIds}\n" +
                    "        </foreach>\n" +
                    "    </delete>\n" +
                    "    <update id=\"update\" parameterType=\"io.renren.modules.jxgk.entity."+EntityName+"Entity\">\n" +
                    "        update "+DataBaseName+"\n" +
                    "        <set>\n" +
                                Update+
                    "        </set>\n" +
                    "        where "+KeyCamel+" = #{"+Key+"}\n" +
                    "    </update>\n" +
                    "</mapper>";
            FileWriterWeb(MapperCode,"mapper",EntityName+"Dao","xml");
        }
        public void CreateJsCode(){
            String [] JsonContent=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                if(EntityProperty[i].equals(SearchKey)){
                    JsonContent[i]="            { label: '"+EntityChineseProperty[i]+"'," +
                            " name: '"+EntityProperty[i]+"', width: 40,key: true },\n ";
                }
                else if(i==EntityPropertyNum-1){
                    JsonContent[i]="            { label: '"+EntityChineseProperty[i]+"'," +
                            " name: '"+EntityProperty[i]+"', width: 40 }\n ";
                }else{
                    JsonContent[i]="            { label: '"+EntityChineseProperty[i]+"'," +
                            " name: '"+EntityProperty[i]+"', width: 40 },\n ";
                }
            }
            String Json="";
            for(int i=0;i<EntityPropertyNum;i++){
                Json+=JsonContent[i];
            }
            String [] ValidatorContent=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                ValidatorContent[i]="            if(isBlank(vm."+EntityName+"."+EntityProperty[i]+")){\n" +
                        "                alert(\""+EntityChineseProperty[i]+"不能为空\");\n" +
                        "                return true;\n" +
                        "            }\n" ;
            }
            String Validator="";
            for(int i=0;i<EntityPropertyNum;i++){
                Validator+=ValidatorContent[i];
            }
            String JsCode="$(function () {\n" +
                    "    $(\"#jqGrid\").jqGrid({\n" +
                    "        url: baseURL + '"+EntityName+"/Select',\n" +
                    "        datatype: \"json\",\n" +
                    "        colModel: [\n" +
                                Json+
                    "        ],\n" +
                    "        viewrecords: true,\n" +
                    "        height: 385,\n" +
                    "        rowNum: 10,\n" +
                    "        rowList : [10,30,50],\n" +
                    "        rownumbers: true,\n" +
                    "        rownumWidth: 25,\n" +
                    "        autowidth:true,\n" +
                    "        multiselect: true,\n" +
                    "        pager: \"#jqGridPager\",\n" +
                    "        jsonReader : {\n" +
                    "            root: \"page.list\",\n" +
                    "            page: \"page.currPage\",\n" +
                    "            total: \"page.totalPage\",\n" +
                    "            records: \"page.totalCount\"\n" +
                    "        },\n" +
                    "        prmNames : {\n" +
                    "            page:\"page\",\n" +
                    "            rows:\"limit\",\n" +
                    "            order: \"order\"\n" +
                    "        },\n" +
                    "        gridComplete:function(){\n" +
                    "            //隐藏grid底部滚动条\n" +
                    "            $(\"#jqGrid\").closest(\".ui-jqgrid-bdiv\").css({ \"overflow-x\" : \"hidden\" });\n" +
                    "        }\n" +
                    "    });\n" +
                    "});\n" +
                    "\n" +
                    "var vm = new Vue({\n" +
                    "    el:'#rrapp',\n" +
                    "    data:{\n" +
                    "        q:{\n" +
                    "            key: null\n" +
                    "        },\n" +
                    "        showList: true,\n" +
                    "        title: null,\n" +
                    "        "+EntityName+": {},\n" +
                    "        "+Key+": null\n" +
                    "    },\n" +
                    "    methods: {\n" +
                    "        query: function () {\n" +
                    "            vm.reload();\n" +
                    "        },\n" +
                    "        add: function(){\n" +
                    "            vm.showList = false;\n" +
                    "            vm.title = \"新增\";\n" +
                    "            vm."+EntityName+" = {};\n" +
                    "        },\n" +
                    "        update: function () {\n" +
                    "            var id = getSelectedRow();\n" +
                    "            if(id == null){\n" +
                    "                return ;\n" +
                    "            }\n" +
                    "            $.get(baseURL + \""+EntityName+"/Info/\"+id, function(r){\n" +
                    "                vm.showList = false;\n" +
                    "                vm.title = \"修改\";\n" +
                    "                vm."+EntityName+" = r."+EntityName+";\n" +
                    "                vm."+Key+" = r."+EntityName+"."+Key+";\n" +
                    "            });\n" +
                    "        },\n" +
                    "        del: function () {\n" +
                    "            var ids = getSelectedRows();\n" +
                    "            if(ids == null){\n" +
                    "                return ;\n" +
                    "            }\n" +
                    "            confirm('确定要删除选中的记录？', function(){\n" +
                    "                $.ajax({\n" +
                    "                    type: \"POST\",\n" +
                    "                    url: baseURL + \""+EntityName+"/Delete\",\n" +
                    "                    contentType: \"application/json\",\n" +
                    "                    data: JSON.stringify(ids),\n" +
                    "                    success: function(r){\n" +
                    "                        if(r.code == 0){\n" +
                    "                            alert('操作成功', function(){\n" +
                    "                                vm.reload();\n" +
                    "                            });\n" +
                    "                        }else{\n" +
                    "                            alert(r.msg);\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                });\n" +
                    "            });\n" +
                    "        },\n" +
                    "        saveOrUpdate: function () {\n" +
                    "            if(vm.validator()){\n" +
                    "                return ;\n" +
                    "            }\n" +
                    "\n" +
                    "            var url = vm."+EntityName+"."+Key+" == vm."+Key+" ? \""+EntityName+"/Update\" : \""+EntityName+"/Insert\";\n" +
                    "            $.ajax({\n" +
                    "                type: \"POST\",\n" +
                    "                url: baseURL + url,\n" +
                    "                contentType: \"application/json\",\n" +
                    "                data: JSON.stringify(vm."+EntityName+"),\n" +
                    "                success: function(r){\n" +
                    "                    if(r.code === 0){\n" +
                    "                        alert('操作成功', function(){\n" +
                    "                            vm.reload();\n" +
                    "                        });\n" +
                    "                    }else{\n" +
                    "                        alert(r.msg);\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            });\n" +
                    "        },\n" +
                    "        reload: function () {\n" +
                    "            vm.showList = true;\n" +
                    "            var page = $(\"#jqGrid\").jqGrid('getGridParam','page');\n" +
                    "            $(\"#jqGrid\").jqGrid('setGridParam',{\n" +
                    "                postData:{ '"+SearchKey+"': vm.q.key},\n" +
                    "                page:page\n" +
                    "            }).trigger(\"reloadGrid\");\n" +
                    "        },\n" +
                    "        validator: function () {\n" +
                                        Validator+
                    "            }\n" +
                    "        }\n" +
                    "});";
            FileWriterWeb(JsCode,"static\\js\\modules",DataBaseName,"js");
        }
        public void CreateHtmlCode(){
            String [] HtmlContent=new String[EntityPropertyNum];
            for(int i=0;i<EntityPropertyNum;i++){
                HtmlContent[i]="            <div class=\"form-group\">\n" +
                        "                <div class=\"col-sm-2 control-label\">"+EntityChineseProperty[i]+"</div>\n" +
                        "                <div class=\"col-sm-10\">\n" +
                        "                    <input type=\"text\" class=\"form-control\" v-model=\""+EntityName+"."+EntityProperty[i]+"\" placeholder=\""+EntityChineseProperty[i]+"\"/>\n" +
                        "                </div>\n" +
                        "            </div>\n" ;
            }
            String Html="";
            for(int i=0;i<EntityPropertyNum;i++){
                Html+=HtmlContent[i];
            }
            String HtmlCode="<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>"+EntityName+"Table</title>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "    <meta content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\" name=\"viewport\">\n" +
                    "    <link rel=\"stylesheet\" href=\"../../css/bootstrap.min.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"../../css/font-awesome.min.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"../../plugins/jqgrid/ui.jqgrid-bootstrap.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"../../plugins/ztree/css/metroStyle/metroStyle.css\">\n" +
                    "    <link rel=\"stylesheet\" href=\"../../css/main.css\">\n" +
                    "    <script src=\"../../libs/jquery.min.js\"></script>\n" +
                    "    <script src=\"../../plugins/layer/layer.js\"></script>\n" +
                    "    <script src=\"../../libs/bootstrap.min.js\"></script>\n" +
                    "    <script src=\"../../libs/vue.min.js\"></script>\n" +
                    "    <script src=\"../../libs/validator.min.js\"></script>\n" +
                    "    <script src=\"../../plugins/jqgrid/grid.locale-cn.js\"></script>\n" +
                    "    <script src=\"../../plugins/jqgrid/jquery.jqGrid.min.js\"></script>\n" +
                    "    <script src=\"../../plugins/ztree/jquery.ztree.all.min.js\"></script>\n" +
                    "    <script src=\"../../js/common.js\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div id=\"rrapp\" v-cloak>\n" +
                    "    <div v-show=\"showList\">\n" +
                    "        <div class=\"grid-btn\">\n" +
                    "            <div class=\"form-group col-sm-2\">\n" +
                    "                <input type=\"text\" class=\"form-control\" v-model=\"q.key\" @keyup.enter=\"query\" placeholder=\""+SearchKey+"\">\n" +
                    "            </div>\n" +
                    "            <a class=\"btn btn-default\" @click=\"query\">查询</a>\n" +
                    "            <a class=\"btn btn-primary\" @click=\"add\"><i class=\"fa fa-plus\"></i>&nbsp;新增</a>\n" +
                    "            <a class=\"btn btn-primary\" @click=\"update\"><i class=\"fa fa-pencil-square-o\"></i>&nbsp;修改</a>\n" +
                    "            <a class=\"btn btn-primary\" @click=\"del\"><i class=\"fa fa-trash-o\"></i>&nbsp;删除</a>\n" +
                    "        </div>\n" +
                    "        <table id=\"jqGrid\"></table >\n" +
                    "        <div id=\"jqGridPager\"></div>\n" +
                    "    </div>\n" +
                    "\n" +
                    "    <div v-show=\"!showList\" class=\"panel panel-default\">\n" +
                    "        <div class=\"panel-heading\">{{title}}</div>\n" +
                    "        <form class=\"form-horizontal\">\n" +
                                        Html+
                    "            <div class=\"form-group\">\n" +
                    "                <div class=\"col-sm-2 control-label\"></div>\n" +
                    "                <input type=\"button\" class=\"btn btn-primary\" @click=\"saveOrUpdate\" value=\"确定\"/>\n" +
                    "                &nbsp;&nbsp;<input type=\"button\" class=\"btn btn-warning\" @click=\"reload\" value=\"返回\"/>\n" +
                    "            </div>\n" +
                    "        </form>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "\n" +
                    "<script src=\"../../js/modules/jxgk/"+DataBaseName+".js\"></script>\n" +
                    "</body>\n" +
                    "</html>";
            FileWriterWeb(HtmlCode,"views\\modules",DataBaseName,"html");
        }
		public static void main(String[] args) {
		    String FilePath="D:\\xxx\\xxx\\xxx\\xxx\\src\\main\\";//设置文件根路径
            String EntityName = "xxInfo";//设置实体名称,要跟数据库表名保持一致 例如数据库表名为people_info,则实体名称应当为PeopleInfo,双大写
            String[] EntityProperTy = new String[2];//设置实体字段数组,字段数量任意
            EntityProperTy[0]="xxId";//为每一个字段设置名称,此处必须使用驼峰命名.另外实体的第一个字段默认为主键.
            EntityProperTy[1]="xxName";//驼峰命名
            String[] EntityChineseProperTy = new String[2];//设置实体中文名称数组
            EntityChineseProperTy[0]="xx编号";//实体的中文名称顺序应当与上面实体英文名称一一对应
            EntityChineseProperTy[1]="xx名称";
			//构造函数中的两个数字分别代表查询键和实体字段数量,其中查询键代表页面中模糊查询所使用的字段
			//在例子中查询键为1,则页面中模糊查询使用的字段为“xx名称”
            CodeCreater c = new CodeCreater(FilePath,EntityChineseProperTy,1,EntityName,EntityProperTy,2);
            c.CreateEntityCode();//生成实体代码
            c.CreateControllerCode();//生成Controller代码
            c.CreateDaoCode();//生成DAO代码
            c.CreateServiceCode();//生成Service代码
            c.CreateServiceImplCode();//生成ServiceImpl代码
            c.CreateMapperCode();//生成Mapper代码
            c.CreateJsCode();//生成Js代码
            c.CreateHtmlCode();//生成HTMl代码
		}
}	