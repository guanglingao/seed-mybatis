package org.seed.mybatis.core.support;


import org.seed.mybatis.core.SeedMybatisContext;

/**
 * 支持Active Record模式<br>
 * <pre>
 * {@literal
 * // 保存全部字段
 * @Test
 * public void save() {
 *     UserInfoRecord userInfoRecord = new UserInfoRecord();
 *     userInfoRecord.setUserId(11);
 *     userInfoRecord.setCity("杭州");
 *     userInfoRecord.setAddress("西湖");
 *     boolean success = userInfoRecord.save();
 *     Assert.assertTrue(success);
 * }
 *
 * // 保存不为null的字段
 * @Test
 * public void saveIgnoreNull() {
 *     UserInfoRecord userInfoRecord = new UserInfoRecord();
 *     userInfoRecord.setUserId(11);
 *     userInfoRecord.setCity("杭州");
 *     userInfoRecord.setAddress("西湖");
 *     boolean success = userInfoRecord.saveIgnoreNull();
 *     Assert.assertTrue(success);
 * }
 *
 * // 修改全部字段
 * @Test
 * public void update() {
 *     UserInfoRecord userInfoRecord = new UserInfoRecord();
 *     userInfoRecord.setId(4);
 *     userInfoRecord.setUserId(11);
 *     userInfoRecord.setCity("杭州");
 *     userInfoRecord.setAddress("西湖");
 *     boolean success = userInfoRecord.update();
 *     Assert.assertTrue(success);
 * }
 *
 * // 修改不为null的字段
 * @Test
 * public void updateIgnoreNull() {
 *     UserInfoRecord userInfoRecord = new UserInfoRecord();
 *     userInfoRecord.setId(5);
 *     userInfoRecord.setUserId(11);
 *     userInfoRecord.setCity("杭州");
 *     userInfoRecord.setAddress("西湖");
 *     boolean success = userInfoRecord.updateIgnoreNull();
 *     Assert.assertTrue(success);
 * }
 *
 * // 保存或修改不为null的字段
 * @Test
 * public void saveOrUpdateIgnoreNull() {
 *     UserInfoRecord userInfoRecord = new UserInfoRecord();
 *     userInfoRecord.setUserId(11);
 *     userInfoRecord.setCity("杭州");
 *     userInfoRecord.setAddress("西湖");
 *     boolean success = userInfoRecord.saveOrUpdateIgnoreNull();
 *     Assert.assertTrue(success);
 *     System.out.println("id:" + userInfoRecord.getId());
 * }
 *
 * // 删除记录
 * @Test
 * public void delete() {
 *     UserInfoRecord userInfoRecord = new UserInfoRecord();
 *     userInfoRecord.setId(8);
 *     boolean success = userInfoRecord.delete();
 *     Assert.assertTrue(success);
 * }
 * }
 * </pre>
 */
public interface Record {

    /**
     * 保存全部字段
     *
     * @return 是否保存成功
     */
    default boolean save() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.save(this)) > 0;
    }

    /**
     * 保存不为null的字段
     *
     * @return 是否保存成功
     */
    default boolean saveIgnoreNull() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.saveIgnoreNull(this)) > 0;
    }

    /**
     * 修改全部字段
     *
     * @return 是否修改成功
     */
    default boolean update() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.update(this)) > 0;
    }

    /**
     * 修改不为null的字段
     *
     * @return 是否修改成功
     */
    default boolean updateIgnoreNull() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.updateIgnoreNull(this)) > 0;
    }

    /**
     * 保存或修改全部字段
     *
     * @return 是否成功
     */
    default boolean saveOrUpdate() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.saveOrUpdateWithNull(this)) > 0;
    }

    /**
     * 保存或修改不为null的字段
     *
     * @return 是否成功
     */
    default boolean saveOrUpdateIgnoreNull() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.saveOrUpdate(this)) > 0;
    }

    /**
     * 删除记录（底层根据id删除），在有逻辑删除字段的情况下，做UPDATE操作。
     *
     * @return 是否成功
     */
    default boolean delete() {
        return SeedMybatisContext.getCrudMapperRunner(this.getClass()).run(mapper -> mapper.delete(this)) > 0;
    }

}
