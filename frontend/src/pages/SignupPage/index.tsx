import React, { useContext } from 'react'
import FormTemplate from '../../components/templates/FormTemplate'
import Image from '../../components/atoms/Image'
import { SignUpForm } from '../../components/organisms/SignUpForm'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { BackendUrl } from '../../utils/constants'
import { UserContext } from '../../App'
import { Box } from '@mui/material'

export const SignUpPage = () => {
  const { user, setUser } = useContext(UserContext)
  const navigate = useNavigate()
  const createUser = async (userDetails: {
    name: string
    email: string
    password: string
  }) => {
    axios
      .post(BackendUrl + '/users/register', userDetails)
      .then((response) => {
        if (response.status === 201) {
          sessionStorage.setItem('token', response.data.token.token)
          setUser(response.data)
          navigate('/dashboard')
        } else {
          alert('user already exists')
        }
      })
      .catch((error) => {
        alert(error)
      })
  }

  const handleSingup = async (
    name: string,
    email: string,
    password: string
  ) => {
    const userDetails = {
      name: name,
      email: email,
      password: password,
    }
    await createUser(userDetails)
  }
  const leftChildren = (
    <Image src="../assets/icons/signup.svg" alt="singup page" width="100%" />
  )

  const rightChildren = <SignUpForm onClick={handleSingup} />
  return (
    <Box
      onClick={() => {
        console.log(user)
      }}
    >
      <FormTemplate leftChildren={leftChildren} rightChildren={rightChildren} />
    </Box>
  )
}
