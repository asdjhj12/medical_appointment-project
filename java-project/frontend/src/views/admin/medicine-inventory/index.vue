<template>
  <div class="medicine-inventory">
    <h2>药品库存管理</h2>
    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="药品名称">
          <el-input v-model="queryParams.name" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品类别">
          <el-input v-model="queryParams.category" placeholder="请输入药品类别" clearable />
        </el-form-item>
        <el-form-item label="生产厂家">
          <el-input v-model="queryParams.manufacturer" placeholder="请输入生产厂家" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="低库存">
          <el-switch v-model="queryParams.lowStock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 10px;">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增药品</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>
      <el-table
        :data="medicineList"
        v-loading="loading"
        style="width: 100%; margin-top: 8px;"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="药品名称" width="150" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="manufacturer" label="生产厂家" width="150" />
        <el-table-column prop="category" label="药品类别" width="120" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价" width="100" />
        <el-table-column prop="stockQuantity" label="库存数量" width="100">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stockQuantity <= row.minStock }">
              {{ row.stockQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button 
              size="small" 
              type="success" 
              @click="handleStockIn(row)"
            >入库</el-button>
            <el-button 
              size="small" 
              type="warning" 
              @click="handleStockOut(row)"
              :disabled="row.stockQuantity <= 0"
            >出库</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
            >删除</el-button>
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

    <!-- 新增/编辑药品对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogType === 'add' ? '新增药品' : '编辑药品'" 
      width="500px"
    >
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="100px"
      >
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="药品类别" prop="category">
          <el-input v-model="form.category" placeholder="请输入药品类别" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stockQuantity">
          <el-input-number v-model="form.stockQuantity" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number v-model="form.minStock" :min="0" :precision="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 入库/出库对话框 -->
    <el-dialog 
      v-model="stockDialogVisible" 
      :title="stockDialogType === 'in' ? '药品入库' : '药品出库'" 
      width="400px"
    >
      <el-form 
        :model="stockForm" 
        :rules="stockRules" 
        ref="stockFormRef" 
        label-width="100px"
      >
        <el-form-item label="药品名称">
          <el-input v-model="stockForm.medicineName" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input v-model="stockForm.currentStock" disabled />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number 
            v-model="stockForm.quantity" 
            :min="1" 
            :precision="0"
            :max="stockDialogType === 'out' ? stockForm.currentStock : undefined"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="stockForm.remark" 
            type="textarea" 
            placeholder="请输入备注" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStockForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getMedicineInventoryList, 
  getMedicineInventoryById, 
  addMedicineInventory, 
  updateMedicineInventory, 
  deleteMedicineInventory,
  updateStockQuantity
} from '@/api/medicineInventory'
import { useRouter } from 'vue-router'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  name: '',
  category: '',
  manufacturer: '',
  status: undefined,
  lowStock: false,
  pageNum: 1,
  pageSize: 10
})

// 药品列表数据
const medicineList = ref<any[]>([])
const loading = ref(false)
const total = ref(0)

// 表单数据
const dialogVisible = ref(false)
const dialogType = ref('add')
const form = reactive({
  id: undefined,
  name: '',
  specification: '',
  manufacturer: '',
  category: '',
  unit: '',
  price: 0,
  stockQuantity: 0,
  minStock: 0,
  status: 1
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'change' }],
  stockQuantity: [{ required: true, message: '请输入库存数量', trigger: 'change' }],
  minStock: [{ required: true, message: '请输入最低库存', trigger: 'change' }]
}

// 库存操作表单数据
const stockDialogVisible = ref(false)
const stockDialogType = ref('in')
const stockForm = reactive({
  id: undefined,
  medicineName: '',
  currentStock: 0,
  quantity: 1,
  remark: ''
})

// 库存操作表单校验规则
const stockRules = {
  quantity: [
    { required: true, message: '请输入数量', trigger: 'change' },
    { type: 'number', min: 1, message: '数量必须大于0', trigger: 'change' }
  ]
}

const formRef = ref()
const stockFormRef = ref()

// 查询药品列表
const getMedicines = async () => {
  loading.value = true
  try {
    const res = await getMedicineInventoryList(queryParams)
    console.log('药品库存接口返回：', res)
    if (Array.isArray(res)) {
      medicineList.value = res
      total.value = res.length
    } else if (res && typeof res === 'object' && 'data' in res) {
      const data = res.data as any
      medicineList.value = data?.records || []
      total.value = data?.total || 0
    } else if (res && typeof res === 'object' && 'records' in res) {
      const data = res as any
      medicineList.value = data.records || []
      total.value = data.total || 0
    } else {
      medicineList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取药品列表失败:', error)
    medicineList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 处理查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getMedicines()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.name = ''
  queryParams.category = ''
  queryParams.manufacturer = ''
  queryParams.status = undefined
  queryParams.lowStock = false
  handleQuery()
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.pageSize = size
  getMedicines()
}

// 处理当前页变化
const handleCurrentChange = (page: number) => {
  queryParams.pageNum = page
  getMedicines()
}

// 处理刷新
const handleRefresh = () => {
  getMedicines()
}

// 处理新增药品
const handleAdd = () => {
  dialogType.value = 'add'
  form.id = undefined
  form.name = ''
  form.specification = ''
  form.manufacturer = ''
  form.category = ''
  form.unit = ''
  form.price = 0
  form.stockQuantity = 0
  form.minStock = 0
  form.status = 1
  dialogVisible.value = true
}

// 处理编辑药品
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  // 检查Token状态
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('您尚未登录或登录已过期，请重新登录')
    router.push('/login')
    return
  }

  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    try {
      if (dialogType.value === 'add') {
        await addMedicineInventory(form)
        ElMessage.success('新增药品成功')
      } else {
        await updateMedicineInventory(form)
        ElMessage.success('更新药品成功')
      }
      dialogVisible.value = false
      getMedicines()
    } catch (error: any) {
      console.error('操作失败:', error)
      // 避免重复显示错误消息
      if (!error.isHandled) {
        // 如果是401错误，由请求拦截器处理
        if (error.response && error.response.status !== 401) {
          ElMessage.error(error.message || '操作失败，请稍后重试')
        }
        error.isHandled = true
      }
    }
  })
}

// 处理删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除药品 "${row.name}" 吗?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteMedicineInventory(row.id)
      ElMessage.success('删除成功')
      getMedicines()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 处理入库
const handleStockIn = (row: any) => {
  stockDialogType.value = 'in'
  stockForm.id = row.id
  stockForm.medicineName = row.name
  stockForm.currentStock = row.stockQuantity
  stockForm.quantity = 1
  stockForm.remark = ''
  stockDialogVisible.value = true
}

// 处理出库
const handleStockOut = (row: any) => {
  stockDialogType.value = 'out'
  stockForm.id = row.id
  stockForm.medicineName = row.name
  stockForm.currentStock = row.stockQuantity
  stockForm.quantity = 1
  stockForm.remark = ''
  stockDialogVisible.value = true
}

// 提交库存操作表单
const submitStockForm = async () => {
  // 检查Token状态
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('您尚未登录或登录已过期，请重新登录')
    router.push('/login')
    return
  }
  
  stockFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    try {
      const quantityChange = stockDialogType.value === 'in' 
        ? stockForm.quantity 
        : -stockForm.quantity
      
      if (stockForm.id !== undefined) {
        await updateStockQuantity(stockForm.id, quantityChange)
        ElMessage.success(stockDialogType.value === 'in' ? '入库成功' : '出库成功')
        stockDialogVisible.value = false
        getMedicines()
      } else {
        ElMessage.error('药品ID不能为空')
      }
    } catch (error: any) {
      console.error('操作失败:', error)
      // 避免重复显示错误消息
      if (!error.isHandled) {
        // 如果是401错误，由请求拦截器处理
        if (error.response && error.response.status !== 401) {
          ElMessage.error(error.message || '操作失败，请稍后重试')
        }
        error.isHandled = true
      }
    }
  })
}

onMounted(() => {
  getMedicines()
})
</script>

<style scoped>
.medicine-inventory {
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

.low-stock {
  color: #f56c6c;
  font-weight: bold;
}
</style> 