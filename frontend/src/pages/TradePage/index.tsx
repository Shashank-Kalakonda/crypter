import React, { useContext, useEffect, useState } from 'react'
import LandingTemplate from '../../components/templates/LandingTemplate'
import NavBar from '../../components/organisms/NavBar'
import DashboardHeader from '../../components/organisms/DashboardHeader'
import { DashboardFooter } from '../../components/organisms/DashboardFooter'
import { AllAssets } from '../../components/organisms/AllAssets'
import { apiBase } from '../../utils/constants'
import { Box, CircularProgress, Stack, styled } from '@mui/material'
import theme from '../../theme'
import { OutputDataItem, convertData } from './utils'
import axios from 'axios'
import { TradePageLoader, UserContext } from '../../App'
import { useLocation, useNavigate } from 'react-router'
import Loader from '../../utils/helperComponent'

const HeaderBox = () => (
  <Box marginLeft={theme.spacing(5)} marginBottom={theme.spacing(2)}>
    <DashboardHeader
      headerContent={'Trade'}
      sellEnabled={false}
      buyEnabled={false}
    />
  </Box>
)

const AllAssetsBox = styled(Box)`
  overflow-x: clip;
`

const TradePage = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const queryParams = new URLSearchParams(location.search)
  const allAssetsBool = queryParams.get('allAssets') === 'true'
  const watchlistBool = queryParams.get('watchlist') === 'true'
  const [isActive, setIsActive] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const { loadPage } = useContext(TradePageLoader)
  const [allAssetsData, setAllAssetsData] = useState<OutputDataItem[]>([])
  const { user } = useContext(UserContext)
  const fetchData = async () => {
    await axios
      .get(`${apiBase}/cryptos`, {
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem('token')}`,
        },
      })
      .then((res) =>
        convertData(res.data, user.user.id).then((data) => {
          console.log('Crypto data', data)
          setAllAssetsData(data)
          setIsLoading(false)
        })
      )
  }

  useEffect(() => {
    fetchData()
  }, [loadPage])

  const handleTradeRowClick = (params) => {
    console.log(params.row)
    navigate(
      `/details?coinId=${params.row.id}&coinName=${params.row.Name.currency}`
    )
  }
  return (
    <>
      {isLoading ? (
        <Stack height={'100vh'} justifyContent={'center'}>
          <CircularProgress
            sx={{ alignSelf: 'center', justifySelf: 'center' }}
          ></CircularProgress>
        </Stack>
      ) : (
        <LandingTemplate
          sidebar={<NavBar isActive={isActive} setIsActive={setIsActive} />}
          header={<HeaderBox />}
          footer={<DashboardFooter />}
          content={
            <AllAssetsBox
              marginLeft={theme.spacing(5)}
              marginBottom={theme.spacing(2)}
            >
              <AllAssets
                rows={allAssetsData}
                setAllAssetRows={setAllAssetsData}
                allAssetBool={allAssetsBool}
                watchlistBool={watchlistBool}
                handleTradeRowClick={handleTradeRowClick}
              />
            </AllAssetsBox>
          }
        />
      )}
    </>
  )
}

export default TradePage
