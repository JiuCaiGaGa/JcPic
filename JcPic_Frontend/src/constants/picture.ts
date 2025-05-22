/**
 * 图片审核状态
 */

export const PIC_REVIEW_STATUS_ENUM = {
  REVIEWING: 0, // 待审核
  PASS: 1,  // 通过
  REJECT: 2,  // 退回
}

export const PIC_REVIEW_STATUS_MAP = {
  0: '待审核',
  1: '通过',
  2: '退回',
}

export const PIC_REVIEW_STATUS_OPTIONS = Object.keys(PIC_REVIEW_STATUS_MAP).map((key) => {
  return {
    label: PIC_REVIEW_STATUS_MAP[key],
    value: key,
  }
})
