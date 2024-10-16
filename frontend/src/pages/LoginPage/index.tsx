import React, { useContext } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import { Auth0ContextInterface, User, useAuth0 } from '@auth0/auth0-react'
import FormTemplate from '../../components/templates/FormTemplate'
import { BackendUrl } from '../../utils/constants'
import Image from '../../components/atoms/Image'
import Login from '../../components/organisms/Login'
import { UserContext } from '../../App'

export const LoginPage = () => {
  const { loginWithRedirect }: Auth0ContextInterface<User> = useAuth0()
  const { setUser } = useContext(UserContext)
  const navigate = useNavigate()
  const getFormDataOnSubmit = async (formData: any) => {
    await axios
      .post(BackendUrl + '/users/login', formData)
      .then((response) => {
        if (response.status === 200) {
          sessionStorage.setItem('token', response.data.token.token)
          setUser(response.data)
          navigate('/dashboard')
        }
      })
      .catch((e) => {
        alert('Invalid Credentials: ' + e.message)
      })
  }

  const leftChildren = (
    <Image src="../assets/icons/loginimage.svg" alt="login page" width="100%" />
  )
  const rightChildren = (
    <Login
      handleGoogleLogin={() => {
        loginWithRedirect({
          appState: {
            returnTo: '/dashboard',
          },
          authorizationParams: {
            connection: 'google-oauth2',
          },
        })
      }}
      getFormDataOnSubmit={getFormDataOnSubmit}
    />
  )

  return (
    <FormTemplate leftChildren={leftChildren} rightChildren={rightChildren} />
  )
}
