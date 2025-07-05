<template>
  <div class="medicine-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>药品管理</span>
          <el-button type="primary" @click="handleAdd">新增药品</el-button>
        </div>
      </template>

      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品类型" prop="type">
          <el-input v-model="queryParams.type" placeholder="请输入药品类型" clearable />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="queryParams.manufacturer" placeholder="请输入生产厂家" clearable />
        </el-form-item>
        <el-form-item label="库存状态" prop="stockStatus">
          <el-select v-model="queryParams.stockStatus" placeholder="请选择库存状态" clearable>
            <el-option label="库存不足" :value="0" />
            <el-option label="库存充足" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="下架" :value="0" />
            <el-option label="上架" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="medicineList" border>
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column prop="name" label="药品名称" />
        <el-table-column prop="type" label="药品类型" />
        <el-table-column prop="specification" label="规格" />
        <el-table-column prop="manufacturer" label="生产厂家" />
        <el-table-column prop="stock" label="当前库存" />
        <el-table-column prop="minStock" label="最低库存" />
        <el-table-column prop="unit" label="单位" />
        <el-table-column prop="price" label="单价">
          <template #default="scope">
            {{ scope.row.price }}元
          </template>
        </el-table-column>
        <el-table-column prop="stockStatus" label="库存状态">
          <template #default="scope">
            <el-tag :type="scope.row.stockStatus === 0 ? 'danger' : 'success'">
              {{ scope.row.stockStatus === 0 ? '库存不足' : '库存充足' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'info' : 'success'">
              {{ scope.row.status === 0 ? '下架' : '上架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" link @click="handleStockIn(scope.row)">入库</el-button>
            <el-button type="warning" link @click="handleStockOut(scope.row)">出库</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)" v-if="hasRole('ADMIN')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="medicineForm"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="药品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="药品类型" prop="type">
          <el-input v-model="form.type" placeholder="请输入药品类型" />
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="当前库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number v-model="form.minStock" :min="0" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">下架</el-radio>
            <el-radio :label="1">上架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 入库对话框 -->
    <el-dialog
      title="药品入库"
      v-model="stockInDialog.visible"
      width="400px"
      append-to-body
    >
      <el-form
        ref="stockInForm"
        :model="stockInForm"
        :rules="stockInRules"
        label-width="100px"
      >
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="stockInForm.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="stockInForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitStockIn">确 定</el-button>
          <el-button @click="cancelStockIn">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog
      title="药品出库"
      v-model="stockOutDialog.visible"
      width="400px"
      append-to-body
    >
      <el-form
        ref="stockOutForm"
        :model="stockOutForm"
        :rules="stockOutRules"
        label-width="100px"
      >
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="stockOutForm.quantity" :min="1" :max="currentMedicine.stock" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="stockOutForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitStockOut">确 定</el-button>
          <el-button @click="cancelStockOut">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMedicineList, createMedicine, updateMedicine, deleteMedicine, stockIn, stockOut } from '@/api/medicine'
import { hasRole } from '@/utils/auth'

// Define interfaces for data structures
interface MedicineItem {
  id: number;
  name: string;
  type: string;
  specification: string;
  manufacturer: string;
  stock: number;
  minStock: number;
  unit: string;
  price: number;
  stockStatus: number;
  status: number;
}

interface QueryParams {
  pageNum: number;
  pageSize: number;
  name?: string;
  type?: string;
  manufacturer?: string;
  stockStatus?: number;
  status?: number;
}

interface MedicineForm {
  id?: number;
  name: string;
  type: string;
  specification: string;
  manufacturer: string;
  stock: number;
  minStock: number;
  unit: string;
  price: number;
  status: number;
}

interface StockInForm {
  medicineId?: number;
  quantity: number;
  remark: string;
}

interface StockOutForm {
  medicineId?: number;
  quantity: number;
  remark: string;
}

// Import FormInstance type
import type { FormInstance } from 'element-plus'

// 查询参数
const queryParams: QueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  type: '',
  manufacturer: '',
  stockStatus: undefined,
  status: undefined
})

// 数据列表
const medicineList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 表单
const form: MedicineForm = reactive({
  id: undefined,
  name: '',
  type: '',
  specification: '',
  manufacturer: '',
  stock: 0,
  minStock: 0,
  unit: '',
  price: 0,
  status: 1
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  type: [{ required: true, message: '请输入药品类型', trigger: 'blur' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  manufacturer: [{ required: true, message: '请输入生产厂家', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入当前库存', trigger: 'blur' }],
  minStock: [{ required: true, message: '请输入最低库存', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 入库对话框
const stockInDialog = reactive({
  visible: false
})

// 入库表单
const stockInForm: StockInForm = reactive({
  medicineId: undefined,
  quantity: 1,
  remark: ''
})

// 入库表单校验规则
const stockInRules = {
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }]
}

// 出库对话框
const stockOutDialog = reactive({
  visible: false
})

// 出库表单
const stockOutForm: StockOutForm = reactive({
  medicineId: undefined,
  quantity: 1,
  remark: ''
})

// 出库表单校验规则
const stockOutRules = {
  quantity: [{ required: true, message: '请输入出库数量', trigger: 'blur' }]
}

// 当前操作的药品
const currentMedicine = ref<MedicineItem>({
  id: 0,
  name: '',
  type: '',
  specification: '',
  manufacturer: '',
  stock: 0,
  minStock: 0,
  unit: '',
  price: 0,
  stockStatus: 0,
  status: 1,
})

// 获取药品列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getMedicineList(queryParams)
    medicineList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 查询按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.name = ''
  queryParams.type = ''
  queryParams.manufacturer = ''
  queryParams.stockStatus = undefined
  queryParams.status = undefined
  handleQuery()
}

// 新增按钮操作
const handleAdd = () => {
  dialog.title = '新增药品'
  dialog.visible = true
  Object.assign(form, {
    id: undefined,
    name: '',
    type: '',
    specification: '',
    manufacturer: '',
    stock: 0,
    minStock: 0,
    unit: '',
    price: 0,
    status: 1
  })
}

// 编辑按钮操作
const handleEdit = (row: MedicineItem) => {
  dialog.title = '编辑药品'
  dialog.visible = true
  Object.assign(form, row)
}

// 删除按钮操作
const handleDelete = (row: MedicineItem) => {
  ElMessageBox.confirm('确认要删除该药品吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    await deleteMedicine(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

// 入库按钮操作
const handleStockIn = (row: MedicineItem) => {
  currentMedicine.value = row
  stockInForm.medicineId = row.id
  stockInForm.quantity = 1
  stockInForm.remark = ''
  stockInDialog.visible = true
}

// 出库按钮操作
const handleStockOut = (row: MedicineItem) => {
  currentMedicine.value = row
  stockOutForm.medicineId = row.id
  stockOutForm.quantity = 1
  stockOutForm.remark = ''
  stockOutDialog.visible = true
}

// 提交表单
const submitForm = async () => {
  const medicineFormRef = document.querySelector('.medicine-form') as FormInstance | null
  if (!medicineFormRef) return

  await medicineFormRef.validate(async (valid: boolean | undefined) => {
    if (valid) {
      if (form.id) {
        await updateMedicine(form.id, form)
        ElMessage.success('修改成功')
      } else {
        await createMedicine(form)
        ElMessage.success('新增成功')
      }
      dialog.visible = false
      getList()
    }
  })
}

// 取消按钮
const cancel = () => {
  dialog.visible = false
}

// 提交入库
const submitStockIn = async () => {
  const stockInFormRef = document.querySelector('.stock-in-form') as FormInstance | null
  if (!stockInFormRef) return

  await stockInFormRef.validate(async (valid: boolean | undefined) => {
    if (valid) {
      await stockIn(stockInForm.medicineId, stockInForm.quantity)
      ElMessage.success('入库成功')
      stockInDialog.visible = false
      getList()
    }
  })
}

// 取消入库
const cancelStockIn = () => {
  stockInDialog.visible = false
}

// 提交出库
const submitStockOut = async () => {
  const stockOutFormRef = document.querySelector('.stock-out-form') as FormInstance | null
  if (!stockOutFormRef) return

  await stockOutFormRef.validate(async (valid: boolean | undefined) => {
    if (valid) {
      await stockOut(stockOutForm.medicineId, stockOutForm.quantity)
      ElMessage.success('出库成功')
      stockOutDialog.visible = false
      getList()
    }
  })
}

// 取消出库
const cancelStockOut = () => {
  stockOutDialog.visible = false
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.medicine-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 