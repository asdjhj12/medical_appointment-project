<template>
  <div class="medicine-stock-record">
    <h2>药品库存记录</h2>
    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="药品名称">
          <el-input v-model="queryParams.medicineName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryParams.type" placeholder="请选择操作类型" clearable>
            <el-option label="入库" :value="1" />
            <el-option label="出库" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 10px;">
      <div class="toolbar">
        <el-button @click="handleRefresh">刷新</el-button>
      </div>
      <el-table
        :data="recordList"
        v-loading="loading"
        style="width: 100%; margin-top: 8px;"
        border
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="medicineName" label="药品名称" width="120" />
        <el-table-column prop="type" label="操作类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 1 ? 'success' : 'warning'">
              {{ row.type === 1 ? '入库' : '出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column prop="beforeStock" label="操作前库存" width="100" />
        <el-table-column prop="afterStock" label="操作后库存" width="100" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="120" />
        <el-table-column prop="operateTime" label="操作时间" width="180">
          <template #default="scope">
            {{ scope.row.operateTime ? dayjs(scope.row.operateTime).format('YYYY/MM/DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ scope.row.createTime ? dayjs(scope.row.createTime).format('YYYY/MM/DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="scope">
            {{ scope.row.updateTime ? dayjs(scope.row.updateTime).format('YYYY/MM/DD HH:mm:ss') : '' }}
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:currentPage="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStockRecordList } from '@/api/medicineStockRecord'
import dayjs from 'dayjs'

// 查询参数
const queryParams = reactive({
  medicineName: '',
  type: undefined,
  startTime: undefined,
  endTime: undefined,
  pageNum: 1,
  pageSize: 10
})

// 日期范围
const dateRange = ref([])

// 记录列表数据
const recordList = ref([])
const loading = ref(false)
const total = ref(0)

// 查询库存记录列表
const getRecords = async () => {
  loading.value = true
  try {
    const res = await getStockRecordList(queryParams)
    let records = []
    if (res && res.data) {
      records = res.data.records || []
      total.value = res.data.total || 0
    } else if (res && res.records) {
      records = res.records || []
      total.value = res.total || 0
    } else if (Array.isArray(res)) {
      records = res
      total.value = res.length
    } else {
      records = []
      total.value = 0
    }
    // 打印原始数据
    console.log('records:', records)
    // 字段名适配，兼容下划线和大写
    const convertKeys = (item: any) => ({
      ...item,
      createTime: item.createTime || item.create_time || item.CREATE_TIME,
      updateTime: item.updateTime || item.update_time || item.UPDATE_TIME,
      operateTime: item.operateTime || item.operate_time || item.OPERATE_TIME,
    })
    recordList.value = records.map(convertKeys)
    // 打印适配后的数据
    console.log('after convert:', recordList.value)
  } catch (error) {
    console.error('获取库存记录列表失败:', error)
    recordList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val) {
    queryParams.startTime = val[0] ? `${val[0]} 00:00:00` : undefined
    queryParams.endTime = val[1] ? `${val[1]} 23:59:59` : undefined
  } else {
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  }
}

// 处理查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getRecords()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.medicineName = ''
  queryParams.type = undefined
  queryParams.startTime = undefined
  queryParams.endTime = undefined
  dateRange.value = []
  handleQuery()
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  getRecords()
}

// 处理当前页变化
const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  getRecords()
}

// 处理刷新
const handleRefresh = () => {
  getRecords()
}

onMounted(() => {
  getRecords()
})
</script>

<style scoped>
.medicine-stock-record {
  padding: 20px;
}

.filter-container {
  margin-bottom: 10px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 