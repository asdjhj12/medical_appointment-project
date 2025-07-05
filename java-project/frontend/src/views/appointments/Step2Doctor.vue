<template>
  <el-form>
    <el-form-item label="医生">
      <div v-if="selectedDoctor">
        <el-card class="doctor-card-selected">
          <div class="doctor-info">
            <img :src="selectedDoctor._avatar" class="avatar" />
            <div>
              <div>
                <span class="doctor-no">({{ selectedDoctor._doctorNo }})</span>
                <b>{{ selectedDoctor.userName }}</b>
                <span>（{{ selectedDoctor.title }}）</span>
              </div>
              <div>专长：{{ selectedDoctor.specialty || '暂无' }}</div>
              <div>简介：{{ selectedDoctor.introduction || '暂无' }}</div>
            </div>
          </div>
          <el-button type="text" @click="showDialog = true">重新选择</el-button>
        </el-card>
      </div>
      <el-button v-else type="primary" @click="showDialog = true">请选择医生</el-button>
    </el-form-item>
    <el-button @click="$emit('prev')">上一步</el-button>
    <el-button type="primary" @click="$emit('next')" :disabled="!modelValue.doctorId">下一步</el-button>

    <!-- 医生选择弹窗 -->
    <el-dialog v-model="showDialog" title="选择医生" width="800px">
      <el-input v-model="search" placeholder="搜索医生姓名" clearable style="margin-bottom: 16px;" />
      <el-row :gutter="20">
        <el-col :span="12" v-for="(doctor, idx) in filteredDoctors" :key="doctor.id">
          <el-card class="doctor-card" @click="selectDoctor(doctor)" :class="{active: modelValue.doctorId === doctor.id}">
            <div class="doctor-info">
              <img :src="doctor._avatar" class="avatar" />
              <div>
                <div>
                  <span class="doctor-no">(doctor{{ idx + 1 }})</span>
                  <b>{{ doctor.userName }}</b>
                  <span>（{{ doctor.title }}）</span>
                </div>
                <div>专长：{{ doctor.specialty || '暂无' }}</div>
                <div>简介：{{ doctor.introduction || '暂无' }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-dialog>
  </el-form>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { listDoctorsByDepartment } from '@/api/doctor'
// 静态导入本地医生头像
import doctor1 from '@/assets/doctor1.jpg'
import doctor2 from '@/assets/doctor2.jpg'
import doctor3 from '@/assets/doctor3.jpg'
import doctor4 from '@/assets/doctor4.jpg'
import doctor5 from '@/assets/doctor5.jpg'

const props = defineProps(['modelValue'])
const doctors = ref([])
const showDialog = ref(false)
const search = ref('')
const doctorAvatars = [doctor1, doctor2, doctor3, doctor4, doctor5]
const defaultAvatar = doctor1

watch(
  () => props.modelValue.departmentId,
  async (newVal) => {
    if (newVal) {
      const res = await listDoctorsByDepartment(newVal)
      // 增加自动编号和本地头像
      const list = res.data?.records || res.data || res || []
      doctors.value = list.map((d, i) => ({
        ...d,
        _doctorNo: `doctor${i + 1}`,
        _avatar: doctorAvatars[i] || defaultAvatar
      }))
    } else {
      doctors.value = []
    }
    props.modelValue.doctorId = ''
  },
  { immediate: true }
)

const selectedDoctor = computed(() =>
  doctors.value.find(d => d.id === props.modelValue.doctorId)
)

const filteredDoctors = computed(() => {
  if (!search.value) return doctors.value
  return doctors.value.filter(d =>
    (d.userName || '').toLowerCase().includes(search.value.toLowerCase())
  )
})

function selectDoctor(doctor) {
  props.modelValue.doctorId = doctor.id
  showDialog.value = false
}
</script>

<style scoped>
.doctor-card, .doctor-card-selected {
  margin-bottom: 10px;
  cursor: pointer;
  border: 1px solid #eee;
  transition: box-shadow 0.2s;
}
.doctor-card.active, .doctor-card:hover {
  border: 1px solid #409EFF;
  box-shadow: 0 2px 8px #409eff22;
}
.doctor-info {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}
.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
  background: #f5f5f5;
}
.doctor-no {
  color: #999;
  margin-right: 8px;
  font-size: 13px;
}
</style> 