package dev.jch0029987.libretibs.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.motion.widget.Key
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.core.view.isNotEmpty
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import dev.jch0029987.libretibs.BuildConfig
import dev.jch0029987.libretibs.NavDirections
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.compat.PictureInPictureCompat
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.constants.PreferenceKeys
import dev.jch0029987.libretibs.databinding.ActivityMainBinding
import dev.jch0029987.libretibs.enums.ImportFormat
import dev.jch0029987.libretibs.enums.TopLevelDestination
import dev.jch0029987.libretibs.extensions.anyChildFocused
import dev.jch0029987.libretibs.helpers.*
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.dialogs.ErrorDialog
import dev.jch0029987.libretibs.ui.dialogs.ImportTempPlaylistDialog
import dev.jch0029987.libretibs.ui.extensions.onSystemInsets
import dev.jch0029987.libretibs.ui.fragments.AudioPlayerFragment
import dev.jch0029987.libretibs.ui.fragments.DownloadsFragment
import dev.jch0029987.libretibs.ui.fragments.PlayerFragment
import dev.jch0029987.libretibs.ui.models.SearchViewModel
import dev.jch0029987.libretibs.ui.models.SubscriptionsViewModel
import dev.jch0029987.libretibs.ui.preferences.BackupRestoreSettings
import dev.jch0029987.libretibs.ui.preferences.BackupRestoreSettings.Companion.FILETYPE_ANY
import dev.jch0029987.libretibs.util.UpdateChecker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController

    private lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

    private var startFragmentId = R.id.homeFragment

    private val searchViewModel: SearchViewModel by viewModels()
    private val subscriptionsViewModel: SubscriptionsViewModel by viewModels()

    private var savedSearchQuery: String? = null
    private var shouldOpenSuggestions = true

    private var playlistExportFormat: ImportFormat = ImportFormat.NEWPIPE
    private var exportPlaylistId: String? = null
    private val createPlaylistsFile = registerForActivityResult(
        ActivityResultContracts.CreateDocument(FILETYPE_ANY)
    ) { uri ->
        if (uri == null) return@registerForActivityResult

        lifecycleScope.launch(Dispatchers.IO) {
            ImportHelper.exportPlaylists(
                this@MainActivity,
                uri,
                playlistExportFormat,
                selectedPlaylistIds = listOf(exportPlaylistId!!)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (!NetworkHelper.isNetworkAvailable(this)) {
            startActivity(Intent(this, NoInternetActivity::class.java))
            finish()
            return
        }

        val isAppConfigured =
            PreferenceHelper.getBoolean(PreferenceKeys.LOCAL_FEED_EXTRACTION, false) ||
                    PreferenceHelper.getString(PreferenceKeys.FETCH_INSTANCE, "").isNotEmpty()
        if (!isAppConfigured) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.onSystemInsets { _, systemBarInsets ->
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    with(binding.appBarLayout) {
                        setPadding(paddingLeft, systemBarInsets.top, paddingRight, paddingBottom)
                    }
                    with(binding.bottomNav) {
                        setPadding(paddingLeft, paddingTop, paddingRight, systemBarInsets.bottom)
                    }
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        binding.bottomNav.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val transition = binding.root.getTransition(R.id.bottom_bar_transition)
            transition.keyFrameList.forEach { keyFrame ->
                for (key in keyFrame.getKeyFramesForView(binding.bottomNav.id)) {
                    if (key.framePosition == 1) key.setValue(Key.TRANSLATION_Y, binding.bottomNav.height)
                }
                for (key in keyFrame.getKeyFramesForView(binding.container.id)) {
                    if (key.framePosition == 100) key.setValue(Key.TRANSLATION_Y, -binding.bottomNav.height)
                }
            }
            binding.root.scene.setTransition(transition)
        }

        if (PreferenceHelper.getBoolean(PreferenceKeys.AUTOMATIC_UPDATE_CHECKS, false)) {
            lifecycleScope.launch(Dispatchers.IO) { UpdateChecker(this@MainActivity).checkUpdate(false) }
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment = binding.fragment.getFragment<NavHostFragment>()
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        startFragmentId = try { NavBarHelper.applyNavBarStyle(binding.bottomNav) } catch (e: Exception) { R.id.homeFragment }

        navController.graph = navController.navInflater.inflate(R.navigation.nav).also {
            it.setStartDestination(startFragmentId)
        }

        binding.bottomNav.setOnItemReselectedListener {
            if (it.itemId != navController.currentDestination?.id) navigateToBottomSelectedItem(it)
            else tryScrollToTop(navHostFragment.childFragmentManager.fragments.firstOrNull()?.requireView())
        }

        binding.bottomNav.setOnItemSelectedListener { navigateToBottomSelectedItem(it) }

        if (binding.bottomNav.menu.children.none { it.itemId == startFragmentId }) deselectBottomBarItems()

        binding.toolbar.title = ThemeHelper.getStyledAppName(this)

        PreferenceHelper.getErrorLog().ifBlank { null }?.let { if (!BuildConfig.DEBUG) ErrorDialog().show(supportFragmentManager, null) }

        setupSubscriptionsBadge()
        loadIntentData()
        showUserInfoDialogIfNeeded()
    }

    private fun deselectBottomBarItems() {
        binding.bottomNav.menu.setGroupCheckable(0, true, false)
        for (child in binding.bottomNav.menu.children) child.isChecked = false
        binding.bottomNav.menu.setGroupCheckable(0, true, true)
    }

    private fun tryScrollToTop(view: View?) {
        val scrollView = view?.allViews?.firstOrNull { it is ScrollView || it is NestedScrollView || it is RecyclerView }
        when (scrollView) {
            is ScrollView -> scrollView.smoothScrollTo(0, 0)
            is NestedScrollView -> scrollView.smoothScrollTo(0, 0)
            is RecyclerView -> scrollView.smoothScrollToPosition(0)
        }
    }

    private fun setupSubscriptionsBadge() {
        if (!PreferenceHelper.getBoolean(PreferenceKeys.NEW_VIDEOS_BADGE, false)) return

        subscriptionsViewModel.fetchSubscriptions(this)
        subscriptionsViewModel.videoFeed.observe(this) { feed ->
            val lastCheckedFeedTime = PreferenceHelper.getLastCheckedFeedTime(seenByUser = true)
            val lastSeenVideoIndex = feed.orEmpty().filter { !it.isUpcoming }.indexOfFirst { it.uploaded <= lastCheckedFeedTime }
            if (lastSeenVideoIndex < 1) return@observe

            binding.bottomNav.getOrCreateBadge(R.id.subscriptionsFragment).apply {
                number = lastSeenVideoIndex
                backgroundColor = ThemeHelper.getThemeColor(this@MainActivity, androidx.appcompat.R.attr.colorPrimary)
                badgeTextColor = ThemeHelper.getThemeColor(this@MainActivity, com.google.android.material.R.attr.colorOnPrimary)
            }
        }
    }

    private fun isSearchInProgress(): Boolean {
        if (!this::navController.isInitialized) return false
        val id = navController.currentDestination?.id ?: return false
        return id in listOf(R.id.searchFragment, R.id.searchResultFragment, R.id.channelFragment, R.id.playlistFragment)
    }

    override fun invalidateMenu() {
        if (isSearchInProgress()) return
        super.invalidateMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar, menu)
        val searchItem = menu.findItem(R.id.action_search)
        this.searchItem = searchItem
        searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                if (query.toHttpUrlOrNull() != null) {
                    val queryIntent = IntentHelper.resolveType(query.toUri())
                    val didNavigate = navigateToMediaByIntent(queryIntent) {
                        navController.popBackStack(R.id.searchFragment, true)
                        searchItem.collapseActionView()
                    }
                    if (didNavigate) return true
                }
                navController.navigate(NavDirections.showSearchResults(query))
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!shouldOpenSuggestions) return true
                if (searchView.isIconified || binding.bottomNav.menu.children.any { it.itemId == navController.currentDestination?.id }) return true
                val destIds = listOf(R.id.searchResultFragment, R.id.channelFragment, R.id.playlistFragment)
                if (navController.currentDestination?.id in destIds && newText == null) return false
                if (navController.currentDestination?.id != R.id.searchFragment) navController.navigate(R.id.searchFragment, bundleOf(IntentData.query to newText))
                else searchViewModel.setQuery(newText)
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                if (navController.currentDestination?.id != R.id.searchResultFragment) {
                    searchViewModel.setQuery(null)
                    navController.navigate(R.id.openSearch)
                }
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                if (navController.previousBackStackEntry != null) this@MainActivity.onBackPressedDispatcher.onBackPressed()
                return !isSearchInProgress()
            }
        })

        if (savedSearchQuery != null) {
            searchItem.expandActionView()
            searchView.setQuery(savedSearchQuery, true)
            savedSearchQuery = null
        }

        return super.onCreateOptionsMenu(menu)
    }

    fun clearSearchViewFocus(): Boolean {
        if (!this::searchView.isInitialized || !searchView.anyChildFocused()) return false
        searchView.clearFocus()
        return true
    }

    fun setQuerySilent(query: String) {
        if (!this::searchView.isInitialized) return
        shouldOpenSuggestions = false
        searchView.setQuery(query, false)
        shouldOpenSuggestions = true
    }

    fun setQuery(query: String, submit: Boolean) {
        if (::searchView.isInitialized) searchView.setQuery(query, submit)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
            R.id.action_about -> { startActivity(Intent(this, AboutActivity::class.java)); true }
            R.id.action_help -> { startActivity(Intent(this, HelpActivity::class.java)); true }
            R.id.action_donate -> { IntentHelper.openLinkFromHref(this, supportFragmentManager, AboutActivity.DONATE_URL, forceDefaultOpen = true); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadIntentData() {
        if (PictureInPictureCompat.isInPictureInPictureMode(this)) {
            val nIntent = Intent(this, MainActivity::class.java)
            nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(nIntent)
        }

        if (intent?.getBooleanExtra(IntentData.maximizePlayer, false) == true) {
            if (intent?.getBooleanExtra(IntentData.audioOnly, false) == false) {
                runOnPlayerFragment { binding.playerMotionLayout.transitionToStart(); true }
                return
            }
            if (runOnAudioPlayerFragment { binding.playerMotionLayout.transitionToStart(); true }) return
            val offlinePlayer = intent!!.getBooleanExtra(IntentData.offlinePlayer, false)
            NavigationHelper.openAudioPlayerFragment(this, offlinePlayer = offlinePlayer)
            return
        }

        if (navigateToMediaByIntent(intent)) return
        intent?.getStringExtra(IntentData.query)?.let { savedSearchQuery = it }
        if (intent?.getBooleanExtra(IntentData.OPEN_DOWNLOADS, false) == true) { navController.navigate(R.id.downloadsFragment); return }

        intent?.getStringExtra(IntentData.fragmentToOpen)?.let {
            ShortcutManagerCompat.reportShortcutUsed(this, it)
            when (it) {
                TopLevelDestination.Home.route -> navController.navigate(R.id.homeFragment)
                TopLevelDestination.Trends.route -> navController.navigate(R.id.trendsFragment)
                TopLevelDestination.Subscriptions.route -> navController.navigate(R.id.subscriptionsFragment)
                TopLevelDestination.Library.route -> navController.navigate(R.id.libraryFragment)
            }
        }

        if (intent?.getBooleanExtra(IntentData.downloading, false) == true) {
            (supportFragmentManager.fragments.find { it is NavHostFragment })
                ?.childFragmentManager?.fragments?.forEach { (it as? DownloadsFragment)?.bindDownloadService() }
        }
    }

    fun navigateToMediaByIntent(intent: Intent, actionBefore: () -> Unit = {}): Boolean {
        intent.getStringExtra(IntentData.channelId)?.let { actionBefore(); navController.navigate(NavDirections.openChannel(channelId = it)); return true }
        intent.getStringExtra(IntentData.channelName)?.let { actionBefore(); navController.navigate(NavDirections.openChannel(channelName = it)); return true }
        intent.getStringExtra(IntentData.playlistId)?.let { actionBefore(); navController.navigate(NavDirections.openPlaylist(playlistId = it)); return true }
        intent.getStringArrayExtra(IntentData.videoIds)?.let {
            actionBefore()
            ImportTempPlaylistDialog().apply {
                arguments = bundleOf(IntentData.playlistName to intent.getStringExtra(IntentData.playlistName), IntentData.videoIds to it)
            }.show(supportFragmentManager, null)
            return true
        }

        intent.getStringExtra(IntentData.videoId)?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && binding.bottomNav.menu.isNotEmpty()) {
                binding.bottomNav.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        NavigationHelper.navigateVideo(this@MainActivity, it, intent.getLongExtra(IntentData.timeStamp, 0L).toString())
                        binding.bottomNav.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            } else NavigationHelper.navigateVideo(this@MainActivity, it, intent.getLongExtra(IntentData.timeStamp, 0L).toString())
            return true
        }

        return false
    }

    private fun navigateToBottomSelectedItem(item: MenuItem): Boolean {
        if (item.itemId == R.id.subscriptionsFragment) binding.bottomNav.removeBadge(R.id.subscriptionsFragment)
        searchItem.collapseActionView()
        return item.onNavDestinationSelected(navController)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        runOnPlayerFragment { onUserLeaveHint(); true }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        loadIntentData()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (runOnPlayerFragment { onKeyUp(keyCode) }) return true
        return super.onKeyUp(keyCode, event)
    }

    fun runOnPlayerFragment(action: PlayerFragment.() -> Boolean): Boolean {
        return supportFragmentManager.fragments.filterIsInstance<PlayerFragment>().firstOrNull()?.let(action) ?: false
    }

    fun runOnAudioPlayerFragment(action: AudioPlayerFragment.() -> Boolean): Boolean {
        return supportFragmentManager.fragments.filterIsInstance<AudioPlayerFragment>().firstOrNull()?.let(action) ?: false
    }

    fun startPlaylistExport(playlistId: String, playlistName: String, format: ImportFormat, includeTimestamp: Boolean) {
        playlistExportFormat = format
        exportPlaylistId = playlistId
        val fileName = BackupRestoreSettings.getExportFileName(this, format, playlistName, includeTimestamp)
        createPlaylistsFile.launch(fileName)
    }

    private fun showUserInfoDialogIfNeeded() {
        if (BuildConfig.DEBUG) return
        val lastShownVersionCode = PreferenceHelper.getInt(PreferenceKeys.LAST_SHOWN_INFO_MESSAGE_VERSION_CODE, -1)
        val infoMessages = emptyList<Pair<Int, String>>()
        val message = infoMessages.lastOrNull { (versionCode, _) -> versionCode > lastShownVersionCode }?.second ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.update_information)
            .setMessage(message)
            .setNegativeButton(R.string.okay, null)
            .setPositiveButton(R.string.never_show_again) { _, _ ->
                PreferenceHelper.putInt(PreferenceKeys.LAST_SHOWN_INFO_MESSAGE_VERSION_CODE, BuildConfig.VERSION_CODE)
            }
            .show()
    }
    fun getCurrentSearchQuery(): String? {
        return if (this::searchView.isInitialized) {
            searchView.query?.toString()
        } else null
    }

}
