-- public."action" definition

-- Drop table

-- DROP TABLE public."action";

CREATE TABLE public."action" (
                                 id varchar NOT NULL, -- Key
                                 title varchar NOT NULL, -- 显示标题
                                 alt varchar NULL, -- 提示文本
                                 default_value varchar NULL, -- 默认值
                                 "path" varchar NULL, -- 系统路径
                                 param_names varchar NULL, -- 参数名,使用英文逗号分隔
                                 response_type int2 NULL, -- 相应值类型; 1:html,2:json,3:text;4:xml,5:media_stream,6:upload,7:download
                                 description varchar NULL, -- 详细说明
                                 sort_weight int4 NULL DEFAULT 0, -- 排序权重; 大值在前
                                 icon varchar NULL, -- 图标类
                                 style_class varchar NULL, -- 样式
                                 category_title varchar NULL, -- 类别名称
                                 category_id varchar NULL, -- 类别ID
                                 parent_id varchar NULL DEFAULT 0, -- 父级节点, 所属页面
                                 view_type int4 NULL, -- 展示样式; 1:页面,2:菜单组,3:菜单链接,4:按钮或主页面链接,5:媒体或媒体控件按钮
                                 function_id varchar NULL, -- 所属功能模块ID
                                 code_enum int8 NULL, -- 权限码标识; 2^n
                                 leaf_act int2 NULL DEFAULT 0, -- 末端数据操作; 1:是,0:否
                                 enabled int2 NULL DEFAULT 1 -- 启用状态; 1:启用,0:禁用
);
CREATE UNIQUE INDEX action_id_idx ON public.action USING btree (id);
COMMENT ON TABLE public."action" IS '系统行为路径';

-- Column comments

COMMENT ON COLUMN public."action".id IS 'Key';
COMMENT ON COLUMN public."action".title IS '显示标题';
COMMENT ON COLUMN public."action".alt IS '提示文本';
COMMENT ON COLUMN public."action".default_value IS '默认值';
COMMENT ON COLUMN public."action"."path" IS '系统路径';
COMMENT ON COLUMN public."action".param_names IS '参数名,使用英文逗号分隔';
COMMENT ON COLUMN public."action".response_type IS '相应值类型; 1:html,2:json,3:text;4:xml,5:media_stream,6:upload,7:download';
COMMENT ON COLUMN public."action".description IS '详细说明';
COMMENT ON COLUMN public."action".sort_weight IS '排序权重; 大值在前';
COMMENT ON COLUMN public."action".icon IS '图标类';
COMMENT ON COLUMN public."action".style_class IS '样式';
COMMENT ON COLUMN public."action".category_title IS '类别名称';
COMMENT ON COLUMN public."action".category_id IS '类别ID';
COMMENT ON COLUMN public."action".parent_id IS '父级节点, 所属页面';
COMMENT ON COLUMN public."action".view_type IS '展示样式; 1:页面,2:菜单组,3:菜单链接,4:按钮或主页面链接,5:媒体或媒体控件按钮';
COMMENT ON COLUMN public."action".function_id IS '所属功能模块ID';
COMMENT ON COLUMN public."action".code_enum IS '权限码标识; 2^n';
COMMENT ON COLUMN public."action".leaf_act IS '末端数据操作; 1:是,0:否';
COMMENT ON COLUMN public."action".enabled IS '启用状态; 1:启用,0:禁用';


-- public."admin" definition

-- Drop table

-- DROP TABLE public."admin";

CREATE TABLE public."admin" (
                                id varchar NOT NULL, -- key
                                username varchar NOT NULL, -- 用户名
                                "password" varchar NOT NULL, -- 密码
                                password_salt varchar NULL, -- 密码盐值
                                password_hash varchar NULL, -- 密码摘要算法
                                company varchar NULL, -- 所属公司
                                department varchar NULL, -- 所属部门
                                workgroup varchar NULL, -- 所属工作组
                                home_address varchar NULL, -- 住址
                                roles varchar NULL, -- 角色;使用英文逗号分隔
                                cellphone varchar NULL, -- 手机号
                                "name" varchar NULL, -- 姓名,尊称
                                id_card_serial varchar NULL, -- 身份证号
                                create_time timestamptz NULL, -- 创建时间
                                freezed int4 NULL DEFAULT 0, -- 冻结状态; 0:正常使用; 1:已冻结,-1:已删除
                                remove_time timestamptz NULL, -- 删除时间
                                action_code_sum int8 NULL -- 权限码和值
);
CREATE INDEX admin_id_card_serial_idx ON public.admin USING btree (id_card_serial, cellphone, name);
CREATE UNIQUE INDEX admin_id_idx ON public.admin USING btree (id);
CREATE INDEX admin_username_idx ON public.admin USING btree (username);

-- Column comments

COMMENT ON COLUMN public."admin".id IS 'key';
COMMENT ON COLUMN public."admin".username IS '用户名';
COMMENT ON COLUMN public."admin"."password" IS '密码';
COMMENT ON COLUMN public."admin".password_salt IS '密码盐值';
COMMENT ON COLUMN public."admin".password_hash IS '密码摘要算法';
COMMENT ON COLUMN public."admin".company IS '所属公司';
COMMENT ON COLUMN public."admin".department IS '所属部门';
COMMENT ON COLUMN public."admin".workgroup IS '所属工作组';
COMMENT ON COLUMN public."admin".home_address IS '住址';
COMMENT ON COLUMN public."admin".roles IS '角色;使用英文逗号分隔';
COMMENT ON COLUMN public."admin".cellphone IS '手机号';
COMMENT ON COLUMN public."admin"."name" IS '姓名,尊称';
COMMENT ON COLUMN public."admin".id_card_serial IS '身份证号';
COMMENT ON COLUMN public."admin".create_time IS '创建时间';
COMMENT ON COLUMN public."admin".freezed IS '冻结状态; 0:正常使用; 1:已冻结,-1:已删除';
COMMENT ON COLUMN public."admin".remove_time IS '删除时间';
COMMENT ON COLUMN public."admin".action_code_sum IS '权限码和值';


-- public."function" definition

-- Drop table

-- DROP TABLE public."function";

CREATE TABLE public."function" (
                                   id varchar NOT NULL, -- key
                                   "name" varchar NOT NULL, -- 功能模块名称
                                   "level" int4 NULL DEFAULT 0, -- 功能模块层级; 0:顶级功能模块
                                   parent_id varchar NULL, -- 父级模块ID
                                   title varchar NULL, -- 显示名称
                                   alt varchar NULL, -- 显示提示
                                   icon varchar NULL, -- 显示图标类
                                   style_class varchar NULL, -- 样式类
                                   background_img varchar NULL, -- 背景图片
                                   enabled varchar NULL DEFAULT 1 -- 是否启用; 1:是,0:否
);
CREATE UNIQUE INDEX function_id_idx ON public.function USING btree (id);
COMMENT ON TABLE public."function" IS '系统功能';

-- Column comments

COMMENT ON COLUMN public."function".id IS 'key';
COMMENT ON COLUMN public."function"."name" IS '功能模块名称';
COMMENT ON COLUMN public."function"."level" IS '功能模块层级; 0:顶级功能模块';
COMMENT ON COLUMN public."function".parent_id IS '父级模块ID';
COMMENT ON COLUMN public."function".title IS '显示名称';
COMMENT ON COLUMN public."function".alt IS '显示提示';
COMMENT ON COLUMN public."function".icon IS '显示图标类';
COMMENT ON COLUMN public."function".style_class IS '样式类';
COMMENT ON COLUMN public."function".background_img IS '背景图片';
COMMENT ON COLUMN public."function".enabled IS '是否启用; 1:是,0:否';


-- public.privilege definition

-- Drop table

-- DROP TABLE public.privilege;

CREATE TABLE public.privilege (
                                  id varchar NOT NULL, -- key
                                  admin_id varchar NOT NULL, -- 管理员ID
                                  action_id varchar NULL, -- 操作ID
                                  role_group varchar NULL, -- 角色组ID
                                  create_time timestamptz NULL -- 创建时间
);
CREATE INDEX privilege_admin_id_idx ON public.privilege USING btree (admin_id);
CREATE UNIQUE INDEX privilege_id_idx ON public.privilege USING btree (id);
CREATE INDEX privilege_role_group_idx ON public.privilege USING btree (role_group);
COMMENT ON TABLE public.privilege IS '操作用户的权限';

-- Column comments

COMMENT ON COLUMN public.privilege.id IS 'key';
COMMENT ON COLUMN public.privilege.admin_id IS '管理员ID';
COMMENT ON COLUMN public.privilege.action_id IS '操作ID';
COMMENT ON COLUMN public.privilege.role_group IS '角色组ID';
COMMENT ON COLUMN public.privilege.create_time IS '创建时间';


-- public.privilege_special definition

-- Drop table

-- DROP TABLE public.privilege_special;

CREATE TABLE public.privilege_special (
                                          id varchar NOT NULL, -- key
                                          admin_id varchar NOT NULL, -- 操作用户ID
                                          function_id varchar NOT NULL, -- 功能模块ID
                                          description varchar NULL -- 说明
);
CREATE INDEX privilege_special_admin_id_idx ON public.privilege_special USING btree (admin_id);
CREATE UNIQUE INDEX privilege_special_id_idx ON public.privilege_special USING btree (id);
COMMENT ON TABLE public.privilege_special IS '功能模块权限';

-- Column comments

COMMENT ON COLUMN public.privilege_special.id IS 'key';
COMMENT ON COLUMN public.privilege_special.admin_id IS '操作用户ID';
COMMENT ON COLUMN public.privilege_special.function_id IS '功能模块ID';
COMMENT ON COLUMN public.privilege_special.description IS '说明';


-- public.role_group definition

-- Drop table

-- DROP TABLE public.role_group;

CREATE TABLE public.role_group (
                                   id varchar NOT NULL, -- key
                                   "name" varchar NOT NULL, -- 角色、分组名称
                                   action_ids varchar NULL, -- 操作ID; 使用英文逗号分隔
                                   description varchar NULL, -- 备注
                                   enabled int4 NULL DEFAULT 1 -- 是否启用;0:禁用,1:启用
);
CREATE UNIQUE INDEX role_group_id_idx ON public.role_group USING btree (id);
COMMENT ON TABLE public.role_group IS '角色分组';

-- Column comments

COMMENT ON COLUMN public.role_group.id IS 'key';
COMMENT ON COLUMN public.role_group."name" IS '角色、分组名称';
COMMENT ON COLUMN public.role_group.action_ids IS '操作ID; 使用英文逗号分隔';
COMMENT ON COLUMN public.role_group.description IS '备注';
COMMENT ON COLUMN public.role_group.enabled IS '是否启用;0:禁用,1:启用';