
package lu.my.mall.service.impl;

import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.vo.MallIndexConfigGoodsVO;
import lu.my.mall.dao.IndexConfigMapper;
import lu.my.mall.dao.MallGoodsMapper;
import lu.my.mall.entity.IndexConfig;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.service.MallIndexConfigService;
import lu.my.mall.util.BeanUtil;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallIndexConfigServiceImpl implements MallIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private MallGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<MallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<MallIndexConfigGoodsVO> MallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<MallGoods> MallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            MallIndexConfigGoodsVOS = BeanUtil.copyList(MallGoods, MallIndexConfigGoodsVO.class);
            for (MallIndexConfigGoodsVO MallIndexConfigGoodsVO : MallIndexConfigGoodsVOS) {
                String goodsName = MallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = MallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    MallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    MallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return MallIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
