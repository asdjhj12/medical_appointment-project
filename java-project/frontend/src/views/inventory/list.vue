<template>
  <div class="inventory">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品库存</span>
          <div>
            <el-button type="primary" @click="handleAdd">新增药品</el-button>
            <el-button type="success" @click="handleInStock">入库</el-button>
            <el-button type="warning" @click="handleOutStock">出库</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.name" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="searchForm.type" placeholder="请选择药品类型">
            <el-option label="全部" value="" />
            <el-option label="处方药" value="处方药" />
            <el-option label="非处方药" value="非处方药" />
            <el-option label="中药" value="中药" />
            <el-option label="西药" value="西药" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.status" placeholder="请选择库存状态">
            <el-option label="全部" value="" />
            <el-option label="正常" value="NORMAL" />
            <el-option label="不足" value="LOW" />
            <el-option label="过期" value="EXPIRED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="inventoryList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="药品名称" width="150" />
        <el-table-column prop="type" label="药品类型" width="100" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="manufacturer" label="生产厂家" width="180" />
        <el-table-column prop="quantity" label="库存数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价(元)" width="100" />
        <el-table-column prop="expirationDate" label="有效期至" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleView(scope.row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增药品' : '编辑药品'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="药品类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择药品类型">
            <el-option label="处方药" value="处方药" />
            <el-option label="非处方药" value="非处方药" />
            <el-option label="中药" value="中药" />
            <el-option label="西药" value="西药" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="库存预警值" prop="alertThreshold">
          <el-input-number v-model="form.alertThreshold" :min="0" :step="1" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 入库对话框 -->
    <el-dialog
      v-model="inStockDialogVisible"
      title="药品入库"
      width="500px"
    >
      <el-form
        ref="inStockFormRef"
        :model="inStockForm"
        :rules="inStockRules"
        label-width="100px"
      >
        <el-form-item label="药品" prop="medicineId">
          <el-select v-model="inStockForm.medicineId" placeholder="请选择药品">
            <el-option
              v-for="item in inventoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="inStockForm.quantity" :min="1" :step="1" />
        </el-form-item>
        <el-form-item label="批次号" prop="batchNumber">
          <el-input v-model="inStockForm.batchNumber" placeholder="请输入批次号" />
        </el-form-item>
        <el-form-item label="有效期至" prop="expirationDate">
          <el-date-picker
            v-model="inStockForm.expirationDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="inStockForm.supplier" placeholder="请输入供应商" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="inStockForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="inStockDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleInStockSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog
      v-model="outStockDialogVisible"
      title="药品出库"
      width="500px"
    >
      <el-form
        ref="outStockFormRef"
        :model="outStockForm"
        :rules="outStockRules"
        label-width="100px"
      >
        <el-form-item label="药品" prop="medicineId">
          <el-select v-model="outStockForm.medicineId" placeholder="请选择药品">
            <el-option
              v-for="item in inventoryList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="outStockForm.quantity" :min="1" :step="1" />
        </el-form-item>
        <el-form-item label="出库类型" prop="outType">
          <el-select v-model="outStockForm.outType" placeholder="请选择出库类型">
            <el-option label="处方用药" value="处方用药" />
            <el-option label="药品损耗" value="药品损耗" />
            <el-option label="药品过期" value="药品过期" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联处方" prop="prescriptionId">
          <el-input v-model="outStockForm.prescriptionId" placeholder="请输入关联处方ID（可选）" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="outStockForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="outStockDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleOutStockSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
})

// 表格数据
const loading = ref(false)
const inventoryList = ref([
  {
    id: 1,
    name: '阿莫西林胶囊',
    type: '西药',
    specification: '0.25g*24粒',
    manufacturer: '哈药制药总厂',
    quantity: 500,
    unit: '盒',
    price: 15.8,
    expirationDate: '2025-01-01',
    status: 'NORMAL'
  },
  {
    id: 2,
    name: '感冒灵颗粒',
    type: '非处方药',
    specification: '10g*10袋',
    manufacturer: '同仁堂',
    quantity: 20,
    unit: '盒',
    price: 25.5,
    expirationDate: '2024-06-01',
    status: 'LOW'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 表单对话框
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const form = reactive({
  name: '',
  type: '',
  specification: '',
  manufacturer: '',
  unit: '',
  price: 0,
  alertThreshold: 20,
  description: ''
})

// 入库对话框
const inStockDialogVisible = ref(false)
const inStockFormRef = ref<FormInstance>()
const inStockForm = reactive({
  medicineId: undefined,
  quantity: 1,
  batchNumber: '',
  expirationDate: '',
  supplier: '',
  remark: ''
})

// 出库对话框
const outStockDialogVisible = ref(false)
const outStockFormRef = ref<FormInstance>()
const outStockForm = reactive({
  medicineId: undefined,
  quantity: 1,
  outType: '',
  prescriptionId: '',
  remark: ''
})

// 表单验证规则
const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入药品名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择药品类型', trigger: 'change' }
  ],
  specification: [
    { required: true, message: '请输入规格', trigger: 'blur' }
  ],
  manufacturer: [
    { required: true, message: '请输入生产厂家', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' }
  ]
})

// 入库表单验证规则
const inStockRules = reactive<FormRules>({
  medicineId: [
    { required: true, message: '请选择药品', trigger: 'change' }
  ],
  quantity: [
    { required: true, message: '请输入入库数量', trigger: 'blur' }
  ],
  batchNumber: [
    { required: true, message: '请输入批次号', trigger: 'blur' }
  ],
  expirationDate: [
    { required: true, message: '请选择有效期', trigger: 'change' }
  ],
  supplier: [
    { required: true, message: '请输入供应商', trigger: 'blur' }
  ]
})

// 出库表单验证规则
const outStockRules = reactive<FormRules>({
  medicineId: [
    { required: true, message: '请选择药品', trigger: 'change' }
  ],
  quantity: [
    { required: true, message: '请输入出库数量', trigger: 'blur' }
  ],
  outType: [
    { required: true, message: '请选择出库类型', trigger: 'change' }
  ]
})

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'NORMAL':
      return 'success'
    case 'LOW':
      return 'warning'
    case 'EXPIRED':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'NORMAL':
      return '正常'
    case 'LOW':
      return '不足'
    case 'EXPIRED':
      return '过期'
    default:
      return '未知'
  }
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索逻辑
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.type = ''
  searchForm.status = ''
  handleSearch()
}

// 新增药品
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
  form.name = ''
  form.type = ''
  form.specification = ''
  form.manufacturer = ''
  form.unit = ''
  form.price = 0
  form.alertThreshold = 20
  form.description = ''
}

// 编辑药品
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 查看药品
const handleView = (row: any) => {
  // TODO: 实现查看逻辑
  console.log('查看药品：', row)
}

// 删除药品
const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    '确定要删除该药品信息吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现删除逻辑
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 入库
const handleInStock = () => {
  inStockDialogVisible.value = true
  inStockForm.medicineId = undefined
  inStockForm.quantity = 1
  inStockForm.batchNumber = ''
  inStockForm.expirationDate = ''
  inStockForm.supplier = ''
  inStockForm.remark = ''
}

// 出库
const handleOutStock = () => {
  outStockDialogVisible.value = true
  outStockForm.medicineId = undefined
  outStockForm.quantity = 1
  outStockForm.outType = ''
  outStockForm.prescriptionId = ''
  outStockForm.remark = ''
}

// 提交表单
const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现提交逻辑
      console.log('提交表单：', form)
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
}

// 入库提交
const handleInStockSubmit = () => {
  if (!inStockFormRef.value) return
  inStockFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现入库逻辑
      console.log('入库表单：', inStockForm)
      ElMessage.success('入库成功')
      inStockDialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
}

// 出库提交
const handleOutStockSubmit = () => {
  if (!outStockFormRef.value) return
  outStockFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现出库逻辑
      console.log('出库表单：', outStockForm)
      ElMessage.success('出库成功')
      outStockDialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
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
</script>

<style scoped>
.inventory {
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
</style> 