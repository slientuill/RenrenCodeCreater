# RenrenCodeCreater
人人快速开发框架代码生成器

## 注意事项
仅适用于轻量级快速开发框架  
适用于spring-boot + mybatis +maven 框架下，其他任何框架请自行适配
## 主要功能
实现在人人框架下##对数据库表基本的增删改查##前后端所有代码生成
支持对一个主要字段的简单模糊查询
## 代码简介
```
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
```
