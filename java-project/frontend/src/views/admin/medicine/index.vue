<template>
  <div class="medicine-inventory-management">
    <h2>药品库存管理</h2>
    <el-button type="primary" @click="handleAddMedicine">新增药品</el-button>
    <el-table :data="medicineList" v-loading="loading">
      <el-table-column prop="id" label="ID"></el-table-column>
      <el-table-column prop="name" label="药品名称"></el-table-column>
      <el-table-column prop="specification" label="规格"></el-table-column>
      <el-table-column prop="manufacturer" label="制造商"></el-table-column>
      <el-table-column prop="category" label="类别"></el-table-column>
      <el-table-column prop="unit" label="单位"></el-table-column>
      <el-table-column prop="price" label="价格"></el-table-column>
      <el-table-column prop="stockQuantity" label="库存数量"></el-table-column>
      <el-table-column prop="minStock" label="最低库存"></el-table-column>
      <el-table-column prop="status" label="状态">
         <template slot-scope="scope">
           <span>{{ scope.row.status === 1 ? '正常' : '禁用' }}</span>
         </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEditMedicine(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDeleteMedicine(scope.row)">删除</el-button>
          <el-button size="mini" @click="handleUpdateStock(scope.row)">更新库存</el-button>
          <el-button size="mini" @click="handleCheckLowStock(scope.row)">检查低库存</el-button>
        </template>
      </el-table-column>
    </el-table>

    <medicine-form
      :visible.sync="dialogVisible"
      :formData="currentMedicine"
      :isEdit="isEdit"
      @success="fetchMedicines"
    ></medicine-form>
  </div>
</template>

<script>
import { getAllMedicines, deleteMedicine, updateMedicineStock, checkMedicineLowStock } from '@/api/medicine'; // 假设您有对应的API文件和方法
import { ElMessage, ElMessageBox, ElInputNumber } from 'element-plus';
import MedicineForm from './components/MedicineForm.vue';

export default {
  name: 'MedicineInventoryManagement',
  components: {
    MedicineForm,
  },
  data() {
    return {
      medicineList: [],
      loading: false,
      dialogVisible: false,
      currentMedicine: {},
      isEdit: false,
    };
  },
  created() {
    this.fetchMedicines();
  },
  methods: {
    fetchMedicines() {
      this.loading = true;
      getAllMedicines().then(response => {
        // 假设后端返回的数据结构是 { code: 200, data: [...], msg: "..." }
        if (response.code === 200) {
           this.medicineList = response.data;
        } else {
           ElMessage.error('获取药品列表失败: ' + response.msg);
        }
      }).catch(error => {
        ElMessage.error('获取药品列表失败');
        console.error(error);
      }).finally(() => {
        this.loading = false;
      });
    },
    handleAddMedicine() {
      this.isEdit = false;
      this.currentMedicine = {}; // 清空表单数据
      this.dialogVisible = true;
    },
    handleEditMedicine(row) {
      this.isEdit = true;
      this.currentMedicine = { ...row }; // 复制当前行数据到表单
      this.dialogVisible = true;
    },
    handleDeleteMedicine(row) {
       ElMessageBox.confirm(`确定删除药品 "${row.name}"？`, '提示', {
         confirmButtonText: '确定',
         cancelButtonText: '取消',
         type: 'warning',
       }).then(() => {
         deleteMedicine(row.id).then(response => {
            // 假设后端返回的数据结构是 { code: 200, msg: "..." }
            if (response.code === 200) {
              ElMessage.success('删除成功');
              this.fetchMedicines(); // 刷新列表
            } else {
              ElMessage.error('删除失败: ' + response.msg);
            }
         }).catch(error => {
           ElMessage.error('删除失败');
           console.error(error);
         });
       }).catch(() => {
         // 用户取消删除
       });
    },
    handleUpdateStock(row) {
        ElMessageBox.prompt('请输入要增加或减少的库存数量（正数增加，负数减少）', '更新库存', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'number',
          inputValidator: (value) => {
            if (value === null || value === '') return '数量不能为空';
            if (!/^-?\d+$/.test(value)) return '请输入整数';
            return true;
          },
          inputErrorMessage: '请输入有效的整数数量',
        }).then(({ value }) => {
          const quantityChange = parseInt(value);
          updateMedicineStock(row.id, quantityChange).then(response => {
             if (response.code === 200) {
               ElMessage.success('库存更新成功');
               this.fetchMedicines(); // 刷新列表
             } else {
               ElMessage.error('库存更新失败: ' + response.msg);
             }
          }).catch(error => {
            ElMessage.error('库存更新失败');
            console.error(error);
          });
        }).catch(() => {
          // 用户取消操作
        });
    },
    handleCheckLowStock(row) {
       checkMedicineLowStock(row.id).then(response => {
          if (response.code === 200) {
             if (response.data) {
               ElMessage.warning(`药品 "${row.name}" 库存 (${row.stockQuantity}) 低于最低库存 (${row.minStock})`);
             } else {
               ElMessage.info(`药品 "${row.name}" 库存 (${row.stockQuantity}) 正常`);
             }
          } else {
             ElMessage.error('检查低库存失败: ' + response.msg);
          }
       }).catch(error => {
         ElMessage.error('检查低库存失败');
         console.error(error);
       });
    },
  },
};
</script>

<style scoped>
.medicine-inventory-management {
  padding: 20px;
}

.el-table {
  margin-top: 20px;
}
</style> 