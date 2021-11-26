
package lu.my.mall.service.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import lu.my.mall.common.ServiceResultEnum;
import lu.my.mall.controller.vo.MallSearchGoodsVO;
import lu.my.mall.dao.MallGoodsMapper;
import lu.my.mall.entity.MallGoods;
import lu.my.mall.service.MallGoodsService;
import lu.my.mall.util.BeanUtil;
import lu.my.mall.util.PageQueryUtil;
import lu.my.mall.util.PageResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MallGoodsServiceImpl implements MallGoodsService {


    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private MallGoodsMapper goodsMapper;
    @Override
    public PageResult getMallGoodsPage(PageQueryUtil pageUtil) {
        List<MallGoods> goodsList = goodsMapper.findMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
    @Override
    public List<MallGoods> getStreamRecommendations(int userid) {
        if(userid == 0){
            return  null;
        }
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase("recommendation").getCollection("UserRecs");

        Document document = userRecsCollection.find(new Document("userId", userid)).first();
        if(document == null){
            return  null;
        }

        List<MallGoods> recommendations = new ArrayList<>();
        ArrayList<Document> recs = document.get("recs", ArrayList.class);

        for (Document recDoc : recs) {
            MallGoods mallGoods = goodsMapper.selectByPrimaryKey(Long.valueOf(recDoc.getInteger("productId")));
            if(mallGoods != null){
                recommendations.add(mallGoods);
            }
        }
        return recommendations;
    }


    @Override
    public String saveMallGoods(MallGoods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveMallGoods(List<MallGoods> MallGoodsList) {
        if (!CollectionUtils.isEmpty(MallGoodsList)) {
            goodsMapper.batchInsert(MallGoodsList);
        }
    }

    @Override
    public String updateMallGoods(MallGoods goods) {
        MallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public MallGoods getMallGoodsById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchMallGoods(PageQueryUtil pageUtil) {
        List<MallGoods> goodsList = goodsMapper.findMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalMallGoodsBySearch(pageUtil);
        List<MallSearchGoodsVO> MallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            MallSearchGoodsVOS = BeanUtil.copyList(goodsList, MallSearchGoodsVO.class);
            for (MallSearchGoodsVO MallSearchGoodsVO : MallSearchGoodsVOS) {
                String goodsName = MallSearchGoodsVO.getGoodsName();
                String goodsIntro = MallSearchGoodsVO.getGoodsIntro();
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    MallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    MallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(MallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public List<MallGoods> getGoodsRecommendation(Long goodsId) {
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase("recommendation").getCollection("ContentBasedProductRecs");
        Document document = userRecsCollection.find(new Document("goods_id", goodsId)).first();
        if(document == null){
            return  null;
        }
        List<MallGoods> recommendations = new ArrayList<>();
        ArrayList<Document> recs = document.get("recs", ArrayList.class);
        for (Document recDoc : recs) {
            MallGoods mallGoods = goodsMapper.selectByPrimaryKey(Long.valueOf(recDoc.getInteger("goods_id")));
            if(mallGoods != null){
                recommendations.add(mallGoods);
            }
        }
        return recommendations;
    }

    @Override
    public List<MallGoods> getOnlineRec(int userid) {
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase("recommendation").getCollection("StreamRecs");
        Document document = userRecsCollection.find(new Document("userId", userid)).first();
        if(document == null){
            return  null;
        }
        List<MallGoods> recommendations = new ArrayList<>();
        ArrayList<Document> recs = document.get("recs", ArrayList.class);
        for (Document recDoc : recs) {
            MallGoods mallGoods = goodsMapper.selectByPrimaryKey(Long.valueOf(recDoc.getInteger("productId")));
            if(mallGoods != null){
                recommendations.add(mallGoods);
            }
        }
        return recommendations;
    }
}
