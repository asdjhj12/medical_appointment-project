<template>
  <div class="inventory">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>药品库存管理</span>
          <el-button type="primary" @click="handleAdd">新增药品</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.name" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="药品类型">
          <el-select v-model="searchForm.type" placeholder="请选择药品类型">
            <el-option label="中药" value="中药" />
            <el-option label="西药" value="西药" />
            <el-option label="生物制品" value="生物制品" />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="searchForm.status" placeholder="请选择库存状态">
            <el-option label="充足" value="充足" />
            <el-option label="不足" value="不足" />
            <el-option label="告急" value="告急" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="medicineList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="药品名称" width="150" />
        <el-table-column prop="type" label="药品类型" width="100" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="manufacturer" label="生产厂家" width="150" />
        <el-table-column prop="currentStock" label="当前库存" width="100" />
        <el-table-column prop="minStock" label="最低库存" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="scope">
            ¥{{ scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="库存状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="success" link @click="handleStockIn(scope.row)">入库</el-button>
            <el-button type="warning" link @click="handleStockOut(scope.row)">出库</el-button>
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
            <el-option label="中药" value="中药" />
            <el-option label="西药" value="西药" />
            <el-option label="生物制品" value="生物制品" />
          </el-select>
        </el-form-item>
        <el-form-item label="规格" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="生产厂家" prop="manufacturer">
          <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
        </el-form-item>
        <el-form-item label="当前库存" prop="currentStock">
          <el-input-number v-model="form.currentStock" :min="0" />
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
      v-model="stockInDialogVisible"
      title="药品入库"
      width="400px"
    >
      <el-form
        ref="stockInFormRef"
        :model="stockInForm"
        :rules="stockInRules"
        label-width="100px"
      >
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="stockInForm.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="入库日期" prop="date">
          <el-date-picker
            v-model="stockInForm.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="stockInForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockInDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStockInSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 出库对话框 -->
    <el-dialog
      v-model="stockOutDialogVisible"
      title="药品出库"
      width="400px"
    >
      <el-form
        ref="stockOutFormRef"
        :model="stockOutForm"
        :rules="stockOutRules"
        label-width="100px"
      >
        <el-form-item label="出库数量" prop="quantity">
          <el-input-number v-model="stockOutForm.quantity" :min="1" :max="currentMedicine.currentStock" />
        </el-form-item>
        <el-form-item label="出库日期" prop="date">
          <el-date-picker
            v-model="stockOutForm.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="stockOutForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="stockOutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleStockOutSubmit">确定</el-button>
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
const medicineList = ref([
  {
    id: 1,
    name: '阿莫西林胶囊',
    type: '西药',
    specification: '0.25g*24粒',
    manufacturer: '哈药集团制药总厂',
    currentStock: 100,
    minStock: 50,
    unit: '盒',
    price: 15.8,
    status: '充足',
    createTime: '2024-01-01 12:00:00'
  },
  {
    id: 2,
    name: '布洛芬片',
    type: '西药',
    specification: '0.2g*24片',
    manufacturer: '上海信谊药厂',
    currentStock: 30,
    minStock: 50,
    unit: '盒',
    price: 12.5,
    status: '不足',
    createTime: '2024-01-01 12:00:00'
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
  currentStock: 0,
  minStock: 0,
  unit: '',
  price: 0
})

// 入库对话框
const stockInDialogVisible = ref(false)
const stockInFormRef = ref<FormInstance>()
const stockInForm = reactive({
  quantity: 1,
  date: '',
  remark: ''
})

// 出库对话框
const stockOutDialogVisible = ref(false)
const stockOutFormRef = ref<FormInstance>()
const stockOutForm = reactive({
  quantity: 1,
  date: '',
  remark: ''
})

// 当前操作的药品
const currentMedicine = ref({
  id: 0,
  name: '',
  currentStock: 0
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
  currentStock: [
    { required: true, message: '请输入当前库存', trigger: 'blur' }
  ],
  minStock: [
    { required: true, message: '请输入最低库存', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请输入单位', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' }
  ]
})

// 入库表单验证规则
const stockInRules = reactive<FormRules>({
  quantity: [
    { required: true, message: '请输入入库数量', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择入库日期', trigger: 'change' }
  ]
})

// 出库表单验证规则
const stockOutRules = reactive<FormRules>({
  quantity: [
    { required: true, message: '请输入出库数量', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择出库日期', trigger: 'change' }
  ]
})

// 获取状态标签类型
const getStatusType = (status: string) => {
  switch (status) {
    case '充足':
      return 'success'
    case '不足':
      return 'warning'
    case '告急':
      return 'danger'
    default:
      return ''
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
  form.currentStock = 0
  form.minStock = 0
  form.unit = ''
  form.price = 0
}

// 编辑药品
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  dialogVisible.value = true
  Object.assign(form, row)
}

// 入库
const handleStockIn = (row: any) => {
  currentMedicine.value = row
  stockInDialogVisible.value = true
  stockInForm.quantity = 1
  stockInForm.date = ''
  stockInForm.remark = ''
}

// 出库
const handleStockOut = (row: any) => {
  currentMedicine.value = row
  stockOutDialogVisible.value = true
  stockOutForm.quantity = 1
  stockOutForm.date = ''
  stockOutForm.remark = ''
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

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
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

// 提交入库
const handleStockInSubmit = async () => {
  if (!stockInFormRef.value) return
  await stockInFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现入库逻辑
      console.log('入库信息：', {
        medicineId: currentMedicine.value.id,
        ...stockInForm
      })
      ElMessage.success('入库成功')
      stockInDialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
}

// 提交出库
const handleStockOutSubmit = async () => {
  if (!stockOutFormRef.value) return
  await stockOutFormRef.value.validate((valid, fields) => {
    if (valid) {
      // TODO: 实现出库逻辑
      console.log('出库信息：', {
        medicineId: currentMedicine.value.id,
        ...stockOutForm
      })
      ElMessage.success('出库成功')
      stockOutDialogVisible.value = false
    } else {
      console.log('表单验证失败：', fields)
    }
  })
}

// 分页大小改变
const handleSizeChange = (val: number) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val
  // TODO: 重新加载数据
}
</script>

<style lang="scss" scoped>
.inventory {
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
}
</style> 