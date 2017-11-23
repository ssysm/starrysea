package top.starrysea.service;

import top.starrysea.common.Condition;
import top.starrysea.common.ServiceResult;
import top.starrysea.object.dto.Orders;

public interface IOrderService {

	ServiceResult queryAllOrderService(Condition condition, Orders order);

	ServiceResult queryOrderService(Orders order);

	ServiceResult addOrderService(Orders order);

	ServiceResult modifyOrderService(Orders order);

	ServiceResult removeOrderService(Orders order);
}
