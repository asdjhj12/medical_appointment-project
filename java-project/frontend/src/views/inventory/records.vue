<template>
  <div class="inventory-records">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>出入库记录</span>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.medicineName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="记录类型">
          <el-select v-model="searchForm.recordType" placeholder="请选择记录类型">
            <el-option label="全部" value="" />
            <el-option label="入库" value="IN" />
            <el-option label="出库" value="OUT" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="recordList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="recordType" label="记录类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.recordType === 'IN' ? 'success' : 'warning'">
              {{ scope.row.recordType === 'IN' ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="medicineName" label="药品名称" width="150" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="batchNumber" label="批次号" width="120" />
        <el-table-column prop="expirationDate" label="有效期至" width="120" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="记录详情"
      width="600px"
    >
      <div class="record-detail">
        <div class="detail-header">
          <el-tag :type="currentRecord.recordType === 'IN' ? 'success' : 'warning'" size="large">
            {{ currentRecord.recordType === 'IN' ? '入库记录' : '出库记录' }}
          </el-tag>
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="药品名称">{{ currentRecord.medicineName }}</el-descriptions-item>
          <el-descriptions-item label="规格">{{ currentRecord.specification }}</el-descriptions-item>
          <el-descriptions-item label="数量">{{ currentRecord.quantity }} {{ currentRecord.unit }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ currentRecord.createTime }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ currentRecord.operator }}</el-descriptions-item>
          <el-descriptions-item label="批次号" v-if="currentRecord.recordType === 'IN'">{{ currentRecord.batchNumber }}</el-descriptions-item>
          <el-descriptions-item label="有效期至" v-if="currentRecord.recordType === 'IN'">{{ currentRecord.expirationDate }}</el-descriptions-item>
          <el-descriptions-item label="供应商" v-if="currentRecord.recordType === 'IN'">{{ currentRecord.supplier }}</el-descriptions-item>
          <el-descriptions-item label="出库类型" v-if="currentRecord.recordType === 'OUT'">{{ currentRecord.outType }}</el-descriptions-item>
          <el-descriptions-item label="关联处方" v-if="currentRecord.recordType === 'OUT' && currentRecord.prescriptionId">{{ currentRecord.prescriptionId }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentRecord.remark }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  medicineName: '',
  recordType: '',
  dateRange: [] as string[]
})

// 表格数据
const loading = ref(false)
const recordList = ref([
  {
    id: 1,
    recordType: 'IN',
    medicineName: '阿莫西林胶囊',
    specification: '0.25g*24粒',
    quantity: 100,
    unit: '盒',
    batchNumber: 'BN20240101',
    expirationDate: '2025-01-01',
    supplier: '哈药制药总厂',
    operator: '张三',
    createTime: '2024-01-01 10:00:00',
    remark: '正常入库'
  },
  {
    id: 2,
    recordType: 'OUT',
    medicineName: '阿莫西林胶囊',
    specification: '0.25g*24粒',
    quantity: 5,
    unit: '盒',
    outType: '处方用药',
    prescriptionId: 'RX20240105001',
    operator: '李四',
    createTime: '2024-01-05 14:30:00',
    remark: '门诊处方'
  },
  {
    id: 3,
    recordType: 'IN',
    medicineName: '感冒灵颗粒',
    specification: '10g*10袋',
    quantity: 50,
    unit: '盒',
    batchNumber: 'BN20240110',
    expirationDate: '2024-06-01',
    supplier: '同仁堂',
    operator: '张三',
    createTime: '2024-01-10 09:00:00',
    remark: '正常入库'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 详情对话框
const detailDialogVisible = ref(false)
const currentRecord = reactive({
  id: 0,
  recordType: '',
  medicineName: '',
  specification: '',
  quantity: 0,
  unit: '',
  batchNumber: '',
  expirationDate: '',
  supplier: '',
  outType: '',
  prescriptionId: '',
  operator: '',
  createTime: '',
  remark: ''
})

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  searchForm.medicineName = ''
  searchForm.recordType = ''
  searchForm.dateRange = []
  handleSearch()
}

// 查看详情
const handleView = (row: any) => {
  detailDialogVisible.value = true
  Object.assign(currentRecord, row)
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // TODO: 重新加载数据
}

// 导出记录
const handleExport = () => {
  // TODO: 实现导出逻辑
  ElMessage.success('导出成功')
}
</script>

<style scoped>
.inventory-records {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-detail {
  padding: 10px;
}

.detail-header {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}
</style> 