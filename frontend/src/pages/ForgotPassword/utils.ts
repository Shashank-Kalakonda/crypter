import axios from 'axios'
import { apiBase } from '../../utils/constants'

export const verifyEmailFromServer = async (
  email: string
): Promise<{ data: Array<object>; status: number }> => {
  try {
    const response = await axios.get(`${apiBase}/users/verifyEmail`, {
      params: {
        email: email,
      },
    })

    return { data: response.data, status: response.status }
  } catch (error) {
    console.log('Error', error)
    throw error
  }
}
export const verifyCodeFromServer = async (
  email: string,
  resetCode: string
): Promise<{ data: Array<object>; status: number }> => {
  try {
    const response = await axios.post(`${apiBase}/users/verifyOtp`, {
      // params: {
      email: email,
      otp: resetCode,
      // },
    })
    return { data: response.data, status: response.status }
  } catch (error) {
    console.log('Error', error)
    throw error
  }
}

export const changePassword = async (email: string, password: string) => {
  try {
    const token = getToken()
    const response = await axios.patch(
      `${apiBase}/users/resetPassword`,
      {
        email: email,
        password: password,
      },
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    )
    return { status: response.status }
  } catch (error) {
    console.error(error)
  }
}

export const getToken = () => {
  return sessionStorage.getItem('token')
}
