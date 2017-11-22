package top.starrysea.service.impl;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import top.starrysea.common.Common;
import top.starrysea.common.Condition;
import top.starrysea.common.DaoResult;
import top.starrysea.common.ServiceResult;
import top.starrysea.dao.IWorkDao;
import top.starrysea.entity.Work;
import top.starrysea.service.IWorkService;

import static top.starrysea.dao.impl.WorkDaoImpl.PAGE_LIMIT;

@Service("workService")
public class WorkServiceImpl implements IWorkService {
	@Autowired
	private IWorkDao workDao;
	private static final String FILE_ROOT = "D:" + File.separator + "starrysea" + File.separator;

	@Override
	// 查询所有作品
	public ServiceResult queryAllWorkService(Condition condition, Work work) {
		ServiceResult result = new ServiceResult();
		DaoResult daoResult = workDao.getAllWorkDao(condition, work);
		if (!daoResult.isSuccessed()) {
			return new ServiceResult(daoResult);
		}
		List<Work> workList = (List<Work>) daoResult.getResult();
		if (workList.size() == 0) {
			return new ServiceResult("查询结果为空");
		}
		int totalPage = 0;
		daoResult = workDao.getWorkCountDao(condition, work);
		int count = (int) daoResult.getResult();
		if (count % PAGE_LIMIT == 0) {
			totalPage = count / PAGE_LIMIT;
		} else {
			totalPage = (count / PAGE_LIMIT) + 1;
		}
		result.setSuccessed(true);
		result.setResult(workList);
		result.setNowPage(condition.getPage());
		result.setTotalPage(totalPage);
		return result;
	}

	@Override
	// 查询一个作品的详情页
	public ServiceResult queryWorkService(Work work) {
		ServiceResult result = new ServiceResult();
		DaoResult daoResult = workDao.getWorkDao(work);
		if (!daoResult.isSuccessed()) {
			return new ServiceResult(daoResult);
		}
		Work w = (Work) daoResult.getResult();
		result.setSuccessed(true);
		result.setResult(w);
		return result;
	}

	@Override
	// 添加一个作品
	public ServiceResult addWorkService(MultipartFile file, Work work) {
		if (!file.isEmpty()) {
			if ((file.getName().substring(file.getName().lastIndexOf(".") + 1)).equalsIgnoreCase("pdf")) {
				double fileSize = (double) file.getSize() / (double) (1024 * 1024);
				System.out.println(fileSize);
				if (!(fileSize > 10)) {

					String filePath = FILE_ROOT + work.getWorkName() + Common.getCharId(5) + ".pdf";
					try {
						file.transferTo(new File(filePath));

						work.setWorkPdfpath(filePath);
						return new ServiceResult(workDao.saveWorkDao(work));
					} catch (Exception e) {
						return new ServiceResult("文件上传失败");
					}
				} else {
					return new ServiceResult("文件不得超过10M!");
				}
			} else {
				return new ServiceResult("文件格式不合法");
			}
		} else {
			return new ServiceResult("文件为空文件");
		}
	}

	@Override
	// 删除一个作品
	public ServiceResult removeWorkService(Work work) {
		return new ServiceResult(workDao.deleteWorkDao(work));
	}

}
